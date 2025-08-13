package pl.com.api.service;


import at.qubic.api.crypto.FourQ;
import at.qubic.api.crypto.IdentityUtil;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import pl.com.api.dto.QubicKeyPair;
import pl.com.api.dto.WalletDto;
import pl.com.api.repository.QubicKeyPairRepository;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QubicKeyService {

    private final QubicKeyPairRepository repository;
    private static final IdentityUtil util = new IdentityUtil();
//    private final Map<Long, IdentityUtil> identityUtils = new ConcurrentHashMap<>(30);
//    private final FourQ fourQ;
//    final IdentityUtil identityUtil = identityUtils.computeIfAbsent(Thread.currentThread().threadId(), x -> new IdentityUtil(fourQ));

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public QubicKeyService(QubicKeyPairRepository repository) {
        this.repository = repository;
//        this.fourQ = fourQ;
    }

    public QubicKeyPair generateAndSaveKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("Ed25519", "BC");
        KeyPair kp = kpg.generateKeyPair();

        byte[] privateKey = kp.getPrivate().getEncoded();
        byte[] publicKey = kp.getPublic().getEncoded();

        String addressId = computeAddressId(publicKey);

        QubicKeyPair entity = new QubicKeyPair();
        entity.setPrivateKey(privateKey);
        entity.setPublicKey(publicKey);
        entity.setAddressId(addressId);

        return repository.save(entity);
    }

    public String computeAddressId(byte[] publicKey) {
        SHA3Digest sha3 = new SHA3Digest(256);
        sha3.update(publicKey, 0, publicKey.length);
        byte[] hash = new byte[32];
        sha3.doFinal(hash, 0);
        return Base64.getEncoder().encodeToString(hash);
    }


    // Skrócenie seeda do 32 bajtów za pomocą SHA-256
    private byte[] shortenSeed(String seed) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(seed.getBytes(StandardCharsets.UTF_8));
        return hash;  // Zwraca 32 bajty
    }

    // Generuje klucz prywatny przy użyciu Ed25519 na podstawie seeda
    private byte[] derivePrivateKey(String seed) throws NoSuchAlgorithmException {
        // Skróć seed do 32 bajtów
        byte[] seedBytes = shortenSeed(seed);

        // Stwórz generator Ed25519 i użyj skróconego seeda
        Ed25519PrivateKeyParameters privateKeyParams = new Ed25519PrivateKeyParameters(seedBytes, 0);

        // Zwróć klucz prywatny w formacie binarnym (32 bajty)
        return privateKeyParams.getEncoded();
    }

    // Generuje klucz publiczny na podstawie klucza prywatnego (Ed25519)
    private byte[] derivePublicKey(byte[] privateKey) {
        // Utwórz obiekt Ed25519PrivateKeyParameters z klucza prywatnego
        Ed25519PrivateKeyParameters privateKeyParams = new Ed25519PrivateKeyParameters(privateKey, 0);

        // Pobierz klucz publiczny
        Ed25519PublicKeyParameters publicKeyParams = privateKeyParams.generatePublicKey();

        // Zwróć klucz publiczny w formacie binarnym (32 bajty)
        return publicKeyParams.getEncoded();
    }

    // Metoda do generowania kluczy i zwrócenia ich w formacie Base64
    public WalletDto generateKeys() throws NoSuchAlgorithmException {
        String seed = generateSeed();  // Generowanie seeda
        System.out.println("Seed: " + seed);

        byte[] privKey = derivePrivateKey(seed);  // Generowanie klucza prywatnego
        byte[] pubKey = derivePublicKey(privKey);  // Generowanie klucza publicznego

        // Kodowanie kluczy w Base64
        String privB64 = Base64.getEncoder().encodeToString(privKey);
        String pubB64 = Base64.getEncoder().encodeToString(pubKey);

        System.out.println("Public Key (Base64): " + pubB64);
        System.out.println("Private Key (Base64): " + privB64);

        // Zwracamy dane w formacie JSON (przykład struktury DTO)
        return new WalletDto(pubB64, privB64);
    }
    public static String generateSeed() {
        StringBuilder seed = new StringBuilder(55);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 55; i++) {
            char c = (char) ('a' + random.nextInt(26)); // Losowa litera od 'a' do 'z'
            seed.append(c);
        }
        return seed.toString();
    }
    // Oblicza klucz prywatny na podstawie seeda
    public static byte[] generatePrivateKey(String seed) {
        byte[] seedBytes = seed.getBytes(StandardCharsets.UTF_8);


        return util.getPublicKeyFromSeed(seed);
    }
    // Oblicza klucz publiczny na podstawie klucza prywatnego
    public static byte[] generatePublicKey(byte[] privateKey) {
        Ed25519PrivateKeyParameters privateKeyParams = new Ed25519PrivateKeyParameters(privateKey, 0);
        Ed25519PublicKeyParameters publicKeyParams = privateKeyParams.generatePublicKey();
        return publicKeyParams.getEncoded();
    }
    public static byte[] getPublicKeyFromSeed(String seed) {
        final byte[] subSeed = util.getSubSeedFromSeed(seed);
        final byte[] privateKey = util.getPrivateKeyFromSubSeed(subSeed);

        return util.getPublicKeyFromPrivateKey(privateKey);
    }
    public static byte[] getPrivateKeyFromSeed(String seed) {
        final byte[] subSeed = util.getSubSeedFromSeed(seed);

        return util.getPrivateKeyFromSubSeed(subSeed);
    }
    public static byte[] getPublicKeyFromPrivateKey(byte[] seed) {
        byte[] privateKey = getPrivateKeyFromSubSeed(seed);

        return util.getPublicKeyFromPrivateKey(privateKey);
    }
    public static byte[] getPrivateKeyFromSubSeed(byte[] subSeed) {

        return util.getPrivateKeyFromSubSeed(subSeed);
    } public static String getIdentityFromPublicKey(byte[] publicKey) {

        return util.getIdentityFromPublicKey(publicKey);
    }
    public static byte[]getPublicKeyFromIdentity(String identity) {
        return util.getPublicKeyFromIdentity(identity);
    }
//    public byte[] identityFromSeed(String seed) {
//        return  identityUtil.identityFromSeed(seed);
//    }



    }
