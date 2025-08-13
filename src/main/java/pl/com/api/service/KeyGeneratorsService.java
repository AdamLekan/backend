package pl.com.api.service;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.springframework.stereotype.Service;
import pl.com.api.dto.WalletDto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class KeyGeneratorsService {
    // Generuje losowy seed składający się z 55 małych liter
    private String generateSeed() {
        StringBuilder sb = new StringBuilder(55);
        SecureRandom rnd = new SecureRandom();  // Użyj SecureRandom dla lepszego bezpieczeństwa
        for (int i = 0; i < 55; i++) {
            char c = (char)('a' + rnd.nextInt(26));  // Wybór z liter a-z
            sb.append(c);
        }
        return sb.toString();
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
        byte[] pubKey  = derivePublicKey(privKey);  // Generowanie klucza publicznego

        // Kodowanie kluczy w Base64
        String privB64 = Base64.getEncoder().encodeToString(privKey);
        String pubB64  = Base64.getEncoder().encodeToString(pubKey);

        System.out.println("Public Key (Base64): " + pubB64);
        System.out.println("Private Key (Base64): " + privB64);

        // Zwracamy dane w formacie JSON (przykład struktury DTO)
        return new WalletDto(pubB64, privB64);
    }
//    public static String generateSeed() {
//        byte[] seedBytes = Ed25519Util.generatePrivateKey();
//
//        // Zwracamy seed zakodowany w Base64 (możesz użyć Base32, jeśli wymagane)
//        return Base64.getEncoder().encodeToString(seedBytes);
//    }
}

