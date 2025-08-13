package pl.com.api.util;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.Base64;

public class QubicTransaction {

    public static void createAndPrintTransaction() {
        try {
            // --- STAŁE (do podmiany na Twoje klucze/adresy) ---
            String privateKeyBase64 = "XZ1dpcd/aRH1LZ59ovpi8KKSJTyDsFt5ZYahFNH4Ypk=";
            String recipientAddressBase32 = "3S5EYTIMUUXRVUZD4NQPTUWUAFPTUYX3T4PBN3UF2K7H3FILTCFQ";

            // --- DEKODOWANIE ---
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
            Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(privateKeyBytes, 0);

            byte[] recipientAddress = base32Decode(recipientAddressBase32);
            if (recipientAddress.length != 32) {
                throw new IllegalArgumentException("Adres odbiorcy musi mieć 32 bajty!");
            }

            // --- BUDOWANIE HEADERA ---
            byte version = 1;
            byte txType = 1; // np. nadanie uprawnienia
            long nonce = 0L; // możesz podać swój nonce
            long timestamp = Instant.now().getEpochSecond();

            ByteBuffer headerBuf = ByteBuffer.allocate(1 + 1 + 8 + 8);
            headerBuf.order(ByteOrder.BIG_ENDIAN);
            headerBuf.put(version);
            headerBuf.put(txType);
            headerBuf.putLong(nonce);
            headerBuf.putLong(timestamp);
            byte[] header = headerBuf.array();

            // --- BUDOWANIE PAYLOAD ---
            ByteBuffer payloadBuf = ByteBuffer.allocate(4 + recipientAddress.length);
            payloadBuf.order(ByteOrder.BIG_ENDIAN);
            payloadBuf.putInt(1); // typ uprawnienia MED = 1
            payloadBuf.put(recipientAddress);
            byte[] payload = payloadBuf.array();

            // --- DANE DO PODPISU ---
            ByteBuffer toSign = ByteBuffer.allocate(header.length + payload.length);
            toSign.put(header);
            toSign.put(payload);
            byte[] dataToSign = toSign.array();

            // --- PODPIS ---
            Ed25519Signer signer = new Ed25519Signer();
            signer.init(true, privateKey);
            signer.update(dataToSign, 0, dataToSign.length);
            byte[] signature = signer.generateSignature();

            // --- FINALNA TRANSAKCJA ---
            ByteBuffer finalTx = ByteBuffer.allocate(dataToSign.length + signature.length);
            finalTx.put(dataToSign);
            finalTx.put(signature);
            byte[] signedTx = finalTx.array();

            String encodedTx = Base64.getEncoder().encodeToString(signedTx);
            System.out.println("Długość transakcji (bajtów): " + signedTx.length);
            System.out.println("encodedTransaction:");
            System.out.println(encodedTx);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Proste dekodowanie Base32 (bez zależności zewnętrznych)
    // lub użyj Apache Commons Codec Base32 jak masz w projekcie
    private static byte[] base32Decode(String base32) throws Exception {
        String base32chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        base32 = base32.toUpperCase();

        int numBytes = base32.length() * 5 / 8;
        byte[] result = new byte[numBytes];

        int buffer = 0;
        int bitsLeft = 0;
        int count = 0;

        for (char c : base32.toCharArray()) {
            if (c == '=') break;
            int val = base32chars.indexOf(c);
            if (val < 0) throw new IllegalArgumentException("Invalid base32 char: " + c);

            buffer <<= 5;
            buffer |= val & 31;
            bitsLeft += 5;

            if (bitsLeft >= 8) {
                result[count++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }
        return result;
    }


}
