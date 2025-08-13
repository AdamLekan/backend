package pl.com.api.service;

import at.qubic.api.domain.std.SignedTransaction;
import at.qubic.api.domain.std.Transaction;
import at.qubic.api.domain.std.response.TickInfo;
import at.qubic.api.network.Node;
import at.qubic.api.service.ComputorService;
import at.qubic.api.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.handler.timeout.TimeoutException;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.com.api.model.BroadcastResponse;
import pl.com.api.model.BroadcastTransactionRequest;
import pl.com.api.model.MedicalDataType;
import pl.com.api.util.Ed25519Util;
import pl.com.api.util.GrantTransactionBuilder;
import pl.com.api.util.QubicPayloadUtil;
import pl.com.api.util.QubicTx;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;

@Service
public class QubicAccessService2 {

    @Autowired
    private ComputorService computorService;

    private final String rpcUrl = "https://rpc.qubic.org/v1";
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final QubicService qubicService;

    private final TransactionService transactionService;
    public QubicAccessService2(QubicService qubicService, TransactionService transactionService) {
        this.qubicService = qubicService;
        this.transactionService = transactionService;
    }

    private Object callRpc(String method, Map<String, Object> params) throws Exception {
        ObjectNode payload = mapper.createObjectNode();
        payload.put("jsonrpc", "2.0");
        payload.put("id", 1);
        payload.put("method", method);
        payload.set("params", mapper.valueToTree(params != null ? params : Map.of()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(rpcUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();
        System.out.println(request);
        System.out.println(payload);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
        ObjectNode result = (ObjectNode) mapper.readTree(response.body());
        if (result.has("error")) throw new RuntimeException(result.get("error").toString());

        return result.get("result");
    }

    public int getCurrentTick() throws Exception {
        return Math.toIntExact(qubicService.getLatestTick());

    }

    public boolean hasGrantAccess(String from, String to, String dataKey) throws Exception {
        return (Boolean) callRpc("hasGrantAccess", Map.of("from", from, "to", to, "dataKey", dataKey));
    }

    public List<Map<String, Object>> getGrantHistory(String from, String to, String dataKey) throws Exception {
        return (List<Map<String, Object>>) callRpc("getGrantHistory", Map.of("from", from, "to", to, "dataKey", dataKey));
    }

    public String grantAccess(byte[] privateKeySeed, String from, String to, String dataKey, String dataValue) throws Exception {
        return sendSignedTransaction(privateKeySeed, from, to, dataKey, dataValue, "GRANT");
    }

    public String revokeAccess(byte[] privateKeySeed, String from, String to, String dataKey) throws Exception {
        return sendSignedTransaction(privateKeySeed, from, to, dataKey, "", "REVOKE");
    }

    private String sendSignedTransaction(byte[] privateKeySeed, String from, String to, String dataKey, String dataValue, String type) throws Exception {
        System.out.println("Latest start");

        Node node = new Node("82.197.173.130", 21841);  // Adres IP i port węzła

        // Pobranie informacji o najnowszym ticku z węzła
        TickInfo info = node.getCurrentTickInfo()
                .timeout(Duration.ofSeconds(5))  // Zwiększamy czas oczekiwania na odpowiedź do 5 sekund
                .onErrorResume(TimeoutException.class, e -> {
                    // W przypadku błędu (timeout) zapewniamy fallback z wartością domyślną
                    System.err.println("Timeout occurred while fetching tick info: " + e.getMessage());
                    // Zapewniamy fallbackowy tick z wartością domyślną
                    return Mono.just(TickInfo.builder().tick(0).build());  // Fallbackowy tick 0
                })
                .doOnNext(tickInfo -> System.out.println("Latest tick retrieved: " + tickInfo.getTick()))  // Informacja o ticku
                .block();  // Blokowanie w tym miejscu

        // Jeśli tick wynosi 0, oznacza to, że nie udało się pobrać poprawnego ticku
        if (info.getTick() == 0) {
            System.err.println("Failed to retrieve the latest tick, using fallback.");
        }

        // Wypisanie ostatniego ticku
        System.out.println("Latest tick: " + info.getTick());

        // Tutaj możesz dodać logikę do wysyłania podpisanej transakcji (to jest tylko przykład)
        // Zwracamy "OK" jako sygnał, że metoda zakończyła się powodzeniem
        return "OK";
    }
//    private byte[] signEd25519(byte[] seed, byte[] message) {
//        Ed25519PrivateKeyParameters privKey = new Ed25519PrivateKeyParameters(seed, 0);
//        Ed25519Signer signer = new Ed25519Signer();
//        signer.init(true, privKey);
//        signer.update(message, 0, message.length);
//        return signer.generateSignature();
//    }
//
//    public byte[] generatePrivateKeySeed() {
//        byte[] seed = new byte[32];
//        new SecureRandom().nextBytes(seed);
//        return seed;
//    }







}
