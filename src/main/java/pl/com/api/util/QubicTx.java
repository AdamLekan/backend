package pl.com.api.util;

import java.nio.ByteBuffer;
import java.security.Security;
import java.time.Instant;
import java.util.Base64;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class QubicTx {

    public static void doMo() {
        Security.addProvider(new BouncyCastleProvider());

        // Stałe testowe
        String privateKeyBase64 = "n3vnM5KiFQy4y7tLBRFAlzCTib+mkEKkGVuHzL6y+4Q=";
        String recipientBase32 = "3S5EYTIMUUXRVUZD4NQPTUWUAFPTUYX3T4PBN3UF2K7H3FILTCFQ";

        // Dekodowanie kluczy
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
        Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(privateKeyBytes, 0);

        // HEADER
        ByteBuffer buffer = ByteBuffer.allocate(1 + 1 + 8 + 8); // version + txType + nonce + timestamp
        buffer.put((byte) 1);  // version
        buffer.put((byte) 0);  // txType (transfer)
        buffer.putLong(0);     // nonce
        buffer.putLong(Instant.now().getEpochSecond()); // timestamp
        byte[] header = buffer.array();

        // PAYLOAD
        byte[] payload = new byte[4 + 32];
        payload[0] = 0; payload[1] = 0; payload[2] = 0; payload[3] = 0; // QU amount = 0

        // Decode Base32 recipient
        byte[] recipientBytes = Base32Decoder.decode(recipientBase32);
        System.arraycopy(recipientBytes, 0, payload, 4, 32);

        // SIGNATURE
        byte[] dataToSign = new byte[header.length + payload.length];
        System.arraycopy(header, 0, dataToSign, 0, header.length);
        System.arraycopy(payload, 0, dataToSign, header.length, payload.length);

        Ed25519Signer signer = new Ed25519Signer();
        signer.init(true, privateKey);
        signer.update(dataToSign, 0, dataToSign.length);
        byte[] signature = signer.generateSignature();

        // Final TX = header + payload + signature
        ByteBuffer finalTx = ByteBuffer.allocate(header.length + payload.length + signature.length);
        finalTx.put(header);
        finalTx.put(payload);
        finalTx.put(signature);

        // Zakoduj do Base64
        String encodedTx = Base64.getEncoder().encodeToString(finalTx.array());
        System.out.println("encodedTransaction: " + encodedTx);
        System.out.println("\nGotowe! Wyślij teraz:\n");
        System.out.println("curl -X POST 'https://rpc.qubic.org/v1/broadcast-transaction' \\");
        System.out.println("  -H 'accept: application/json' \\");
        System.out.println("  -H 'Content-Type: application/json' \\");
        System.out.println("  -d '{\"encodedTransaction\": \"" + encodedTx + "\"}'");
    }

    // Prosty Base32 decoder (RFC 4648, bez paddingu)
    static class Base32Decoder {
        private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        public static byte[] decode(String base32) {
            base32 = base32.replaceAll("=", "").toUpperCase();
            ByteBuffer out = ByteBuffer.allocate(base32.length() * 5 / 8);
            int buffer = 0, bitsLeft = 0;
            for (char c : base32.toCharArray()) {
                int val = ALPHABET.indexOf(c);
                if (val == -1) continue;
                buffer <<= 5;
                buffer |= val;
                bitsLeft += 5;
                if (bitsLeft >= 8) {
                    out.put((byte)((buffer >> (bitsLeft - 8)) & 0xFF));
                    bitsLeft -= 8;
                }
            }
            out.flip();
            byte[] result = new byte[out.remaining()];
            out.get(result);
            return result;
        }
    }
}

