package pl.com.api.util;

import java.nio.ByteBuffer;
import java.util.Base64;

public class GrantTransactionBuilder {

    public static byte[] buildTransaction(byte[] fromPubKey, byte[] toPubKey, byte permissionType, long tick) {
        ByteBuffer buffer = ByteBuffer.allocate(32 + 32 + 1 + 8);
        buffer.put(fromPubKey);
        buffer.put(toPubKey);
        buffer.put(permissionType);
        buffer.putLong(tick);
        return buffer.array();
    }

    public static byte[] signTransaction(byte[] transactionData, byte[] privateKey) {
        return Ed25519Util.sign(privateKey, transactionData);
    }

    public static String toBase64Transaction2(byte[] transactionData, byte[] signature) {
        byte[] full = new byte[transactionData.length + signature.length];
        System.arraycopy(transactionData, 0, full, 0, transactionData.length);
        System.arraycopy(signature, 0, full, transactionData.length, signature.length);
        return Base64.getEncoder().encodeToString(full).replaceAll("\\s+", "");
    }
    public static String toBase64Transaction(byte[] transactionData, byte[] signature) {
        byte[] full = new byte[transactionData.length + signature.length];
        System.arraycopy(transactionData, 0, full, 0, transactionData.length);
        System.arraycopy(signature, 0, full, transactionData.length, signature.length);
        String base64 = Base64.getEncoder().encodeToString(full);
        System.out.println("Base64 Transaction: " + base64);
        System.out.println("Length: " + base64.length());
        return base64;
    }
    public static String toHexTransaction(byte[] transactionData, byte[] signature) {
        byte[] full = new byte[transactionData.length + signature.length];
        System.arraycopy(transactionData, 0, full, 0, transactionData.length);
        System.arraycopy(signature, 0, full, transactionData.length, signature.length);
        return Ed25519Util.toHex(full);
    }
}
