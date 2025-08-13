package pl.com.api.service;



import at.qubic.api.domain.qx.Qx;
import at.qubic.api.domain.std.SignedTransaction;
import at.qubic.api.domain.std.response.Entity;
import at.qubic.api.domain.std.response.TickData;
import at.qubic.api.network.*;
import org.apache.commons.codec.binary.Base32;
import pl.com.api.util.PayloadEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import at.qubic.api.domain.std.Transaction;
import at.qubic.api.domain.std.response.TickInfo;
import at.qubic.api.service.ComputorService;
import at.qubic.api.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import pl.com.api.dto.WalletDto;
import pl.com.api.model.*;
import pl.com.api.util.Ed25519Util;
import pl.com.api.util.GrantTransactionBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;

import static pl.com.api.service.QubicKeyService.*;
import static pl.com.api.util.EnumUtils.toByte;

@Service
public class QubicService {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String QUBIC_NODE = "https://testnet-rpc.qubic.org/v1";
    static final String QAC_CONTRACT_ADDRESS="QUKF6TDYPIDWBBXRLTX7V3CRQYSMEQRYOPPIGB2W3DHKNU42RW4J4FPDH7OA";
//    private final UrlBasedViewResolver urlBasedViewResolver;
    @Autowired
    private ComputorService computorService;
//    private final TransactionService transactionService;

    private final Node qubicNode;
    private final KeyGeneratorsService keyGenService;
    private final TransactionService transactionService;
    private final QubicKeyService qubicKeyService;

    @Value("${qubic.api.url}")
    public String qubicApiUrl;


    public boolean createWalletInQubic(WalletDto wallet) {
        Map<String, String> payload = new HashMap<>();
        payload.put("address", wallet.getSeed());
        payload.put("public_key", wallet.getPublicKey());
        payload.put("private_key", wallet.getPrivateKey());
        System.out.println("tworze portfel");
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    QUBIC_NODE + "/generate_address",
                    payload,
                    String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getQubicBalance(String address) {
        try {
            String url = QUBIC_NODE + "/get_balance?address=" + address;
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "Błąd pobierania salda";
        }
    }


    public BroadcastResponse broadcastTransaction(String signedTransactionHex) throws JsonProcessingException {


        String endpoint = qubicApiUrl + "/broadcast-transaction";

        BroadcastTransactionRequest requestBody = new BroadcastTransactionRequest(signedTransactionHex);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = new ObjectMapper().writeValueAsString(requestBody);
        System.out.println("Payload JSON: " + json);
        HttpEntity<BroadcastTransactionRequest> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<BroadcastResponse> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, BroadcastResponse.class);

        return response.getBody();
    }

    public long getLatestTick() {
        String url = qubicApiUrl + "/tick-info";
        System.out.println("url: " + url);
        ResponseEntity<TickInfoResponse> response = restTemplate.getForEntity(url, TickInfoResponse.class);

        return response.getBody().getTickInfo().getTick();
    }

    public static String createSignedTransaction(String privateKeyBase64, byte[] recipientAddress, int permissionType) {
        try {
            // Dekoduj klucz prywatny
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
            Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(privateKeyBytes, 0);

            // Przygotuj dane transakcji
            // Przykładowy format (do dostosowania wg specyfikacji Qubic):
            // [typ uprawnienia (4 bajty)] + [adres odbiorcy (32 bajty)]

            ByteBuffer buffer = ByteBuffer.allocate(4 + recipientAddress.length);
            buffer.putInt(permissionType);
            buffer.put(recipientAddress);
            byte[] transactionData = buffer.array();

            // Podpisz transakcję
            Ed25519Signer signer = new Ed25519Signer();
            signer.init(true, privateKey);
            signer.update(transactionData, 0, transactionData.length);
            byte[] signature = signer.generateSignature();

            // Połącz dane i podpis (np. [dane][podpis])
            ByteBuffer fullTx = ByteBuffer.allocate(transactionData.length + signature.length);
            fullTx.put(transactionData);
            fullTx.put(signature);
            byte[] signedTransaction = fullTx.array();

            // Zakoduj do Base64 i zwróć
            return Base64.getEncoder().encodeToString(signedTransaction);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static byte[] decodeBase32Address(String base32Address) {
        return new Base32().decode(base32Address);
    }

    public static void maketrans(){
        String privateKeyBase64 = "XZ1dpcd/aRH1LZ59ovpi8KKSJTyDsFt5ZYahFNH4Ypk=";
        // Zamień swój adres z formatu Base32 na bajty (32 bajty)
        byte[] recipientAddress = decodeBase32Address("3S5EYTIMUUXRVUZD4NQPTUWUAFPTUYX3T4PBN3UF2K7H3FILTCFQ");

        int permissionType = 1; // MED

        String encodedTransaction = createSignedTransaction(privateKeyBase64, recipientAddress, permissionType);

            System.out.println("Encoded transaction:");
            System.out.println(encodedTransaction);
        }

        public void qubicTry() throws JsonProcessingException {

            byte[] fromPrivateKey = Ed25519Util.generatePrivateKey();
            byte[] fromPublicKey = Ed25519Util.getPublicKey(fromPrivateKey);

            byte[] toPrivateKey = Ed25519Util.generatePrivateKey(); // lub znane
            byte[] toPublicKey = Ed25519Util.getPublicKey(toPrivateKey);

            PermissionPayload payload = new PermissionPayload(Base64.getEncoder().encodeToString(toPublicKey), MedicalDataType.ALL.getCode());

            QubicTransaction encodedTransaction = new QubicTransaction()
                    .setSourcePublicKey(new PublicKey(fromPublicKey))
                    .setDestinationPublicKey(new PublicKey(QAC_CONTRACT_ADDRESS))
                    .setTick(getLatestTick())  // tick, np. pobrany z /v1/status
                    .setInputSize(0)
                    .setAmount(0)
                    .setInputType(777)  // stała liczba oznaczająca 'GRANT_PERMISSION' – zależy od Twojego kontraktu
                    .setPayload(new byte[0]);

            // Zakładamy, że klasa QubicTransaction udostępnia metody getDigest() i setSignature()
            byte[] digest = encodedTransaction.getDigest(); // Pobranie danych do podpisu
            byte[] signature = signEd25519(fromPrivateKey, digest); // Wygenerowanie podpisu
            encodedTransaction.setSignature(signature); // Ustawienie podpisu w transakcji

            String encoded = encodedTransaction.encodeTransactionToBase64();
            System.out.println("Encoded Transaction JSON:");
            System.out.println("{\"encodedTransaction\":\"" + encoded + "\"}");


            BroadcastTransactionRequest requestBody = new BroadcastTransactionRequest(encoded);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<BroadcastTransactionRequest> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<BroadcastResponse> response = restTemplate.exchange(
                    qubicApiUrl + "/broadcast-transaction",
                    HttpMethod.POST,
                    entity,
                    BroadcastResponse.class
            );
        }
    private byte[] signEd25519(byte[] privateKeySeed, byte[] message) {
        Ed25519PrivateKeyParameters privKey = new Ed25519PrivateKeyParameters(privateKeySeed, 0);
        Ed25519Signer signer = new Ed25519Signer();
        signer.init(true, privKey);
        signer.update(message, 0, message.length);
        return signer.generateSignature();
    }

    public QubicService(KeyGeneratorsService keyGenService, TransactionService transactionService, QubicKeyService qubicKeyService) {
        this.keyGenService = keyGenService;
        this.transactionService = transactionService;
        this.qubicKeyService = qubicKeyService;
        // Inicjalizacja klienta Node QubicJ (host i port można pobrać z konfiguracji)
        this.qubicNode = new Node("https://rpc-staging.qubic.org", 21841);  // Użycie publicznego węzła Qubic
    }

    public TickInfo getCurrentTickInfo() {
        TickInfo tickInfo = qubicNode.getCurrentTickInfo().block();
        return tickInfo;
    }
//

    public Mono<SignedTransaction> broadcastTransaction(boolean med1Flag) throws NoSuchAlgorithmException, NoSuchProviderException {

        byte[] payload = new byte[3];
        payload[2] = (byte) (med1Flag ? 0x01 : 0x00);

        long tick = getLatestTick();
        long amount = 0L;

        String seed = generateSeed();
        String seed2 = generateSeed();

        // Generowanie klucza prywatnego
        byte[] privateKey = generatePrivateKey(seed);
        byte[] privateKey2 = generatePrivateKey(seed2);
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey);

        // Generowanie klucza publicznego
        byte[] publicKey = generatePublicKey(privateKey);
        byte[] publicKey2 = generatePublicKey(privateKey2);

        final SignedTransaction transaction = transactionService.createTransaction(
                (int) tick +30,
                seed,
                getIdentityFromPublicKey(publicKey),
                amount,
                (short) 3,
                payload);
        System.out.println(transaction.toString());
        Mono<SignedTransaction> broadcastTransaction=   computorService.sendTransaction(transaction);
        SignedTransaction signedTransaction = broadcastTransaction.block();
        System.out.println(signedTransaction.toString());


        Entity aToB = computorService.getEntity(getIdentityFromPublicKey(publicKey)).block();

        assert aToB != null;
        System.out.println(aToB.toString());
        String fromIdentity= getIdentityFromPublicKey(publicKey);
        String toIdentity= getIdentityFromPublicKey(publicKey2);
        // Pobranie wszystkich transakcji wychodzących z adresu fromIdentity.
        Flux<SignedTransaction> outgoing = getOutgoingTransfers(fromIdentity);
        System.out.println("Wszystkie transakcje wychodzące z adresu: " + fromIdentity);
        for (SignedTransaction tx : outgoing.toIterable()) {
            // Wyświetlenie podstawowych informacji o każdej transakcji.
            System.out.println(tx);
            // (Można tu wywołać np. tx.getId(), tx.getAmount(), tx.getDestinationIdentity(), itp., w zależności od dostępnych metod klasy Transaction)
        }

        // Pobranie i wyświetlenie transakcji wychodzących z fromIdentity do konkretnego adresu toIdentity.
        List<SignedTransaction> outgoingToSpecific = getTransfersFromAToB(fromIdentity, toIdentity);
        System.out.println("\nTransakcje wychodzące z " + fromIdentity + " do " + toIdentity + ":");
        for (SignedTransaction tx : outgoingToSpecific) {
            System.out.println(tx);
        }


        System.out.println("test:");
        gg();

        return  computorService.sendTransaction(transaction);


    }

    /**
     * Pobiera wszystkie transakcje wychodzące (transfery) z danego adresu (identity) w sieci Qubic.
     *
     * @param fromIdentity łańcuch znaków reprezentujący adres (publiczne ID/identity) nadawcy
     * @return lista obiektów Transaction reprezentujących wszystkie transakcje wychodzące z podanego adresu
     */
    public Flux<SignedTransaction> getOutgoingTransfers(String fromIdentity) {
        // Pobieramy informację o bieżącym ticku (numerze taktu) sieci Qubic.
        TickInfo tickData = computorService.getCurrentTickInfo().block();
        assert tickData != null;
        long latestTick = tickData.getTick();
        Flux<SignedTransaction> transactionFlux = null;
        // Iterujemy po wszystkich tickach od genesis do bieżącego, aby zebrać transakcje.
        // (Uwaga: W praktyce, aby uzyskać historyczne transakcje spoza bieżącej epoki, wymagany może być archiwalny węzeł danych.)
        for (long tick = latestTick-50; tick <= latestTick; tick++) {
            // Pobieramy dane danego ticku, które zawierają listę transakcji w tym ticku.
            // Zakładamy istnienie metody getTick(tick) zwracającej obiekt zawierający transakcje (np. listę Transaction).
            if (tickData != null) {
                 transactionFlux = computorService.getTickTransactions(tickData.getTick());
                transactionFlux.filter(signedTransaction -> signedTransaction.getTransaction().getDestinationPublicKey().equals(getPublicKeyFromIdentity(fromIdentity)));
                // Przeglądamy wszystkie transakcje w danym ticku.

            }
        }
        return transactionFlux;
    }

    /**
     * Pobiera wszystkie transakcje wychodzące z adresu `fromIdentity` i filtruje tylko te,
     * których odbiorcą jest adres `toIdentity`.
     *
     * @param fromIdentity adres (identity) nadawcy
     * @param toIdentity adres (identity) odbiorcy, który nas interesuje
     * @return lista obiektów Transaction reprezentujących transakcje wychodzące z fromIdentity do toIdentity
     */
    public List<SignedTransaction> getTransfersFromAToB(String fromIdentity, String toIdentity) {
        // Najpierw pobieramy wszystkie transakcje wychodzące z fromIdentity.
        Flux<SignedTransaction> allOutgoing = getOutgoingTransfers(fromIdentity);
        // Filtrowana lista transakcji, gdzie odbiorcą jest toIdentity.
        List<SignedTransaction> filteredTransfers = new ArrayList<>();
        for (SignedTransaction tx : allOutgoing.toIterable()) {
            // Sprawdzamy, czy docelowy adres transakcji (odbiorca) odpowiada toIdentity.
            if (getIdentityFromPublicKey(tx.getTransaction().getDestinationPublicKey()).equals(toIdentity)) {
                filteredTransfers.add(tx);
            }
        }
        return filteredTransfers;
    }

    public void gg() {
        // Przykładowe adresy (identity) podane w zadaniu:
        String fromIdentity = "CPRRBGAOKPKKFEXMSFDXFZMSPBOBCKFYYIPXXAOFTFURFWBPKJFZBNCHBOAL";
        String toIdentity   = "WCCVAQKVVNNMCCGTSQMQUTCHFMACWSHSHMJLTIOVZBDBJNVQZMDWYRHGIPXB";

        // Inicjalizacja obiektu pomocniczego do pobierania transakcji.

        // Pobranie wszystkich transakcji wychodzących z adresu fromIdentity.
        Flux<SignedTransaction> outgoing = getOutgoingTransfers(fromIdentity);
        System.out.println("Wszystkie transakcje wychodzące z adresu: " + fromIdentity);
        for (SignedTransaction tx : outgoing.toIterable()) {
            // Wyświetlenie podstawowych informacji o każdej transakcji.
            System.out.println(tx);
            // (Można tu wywołać np. tx.getId(), tx.getAmount(), tx.getDestinationIdentity(), itp., w zależności od dostępnych metod klasy Transaction)
        }

        // Pobranie i wyświetlenie transakcji wychodzących z fromIdentity do konkretnego adresu toIdentity.
        List<SignedTransaction> outgoingToSpecific = getTransfersFromAToB(fromIdentity, toIdentity);
        System.out.println("\nTransakcje wychodzące z " + fromIdentity + " do " + toIdentity + ":");
        for (SignedTransaction tx : outgoingToSpecific) {
            System.out.println(tx);
        }
    }
}

