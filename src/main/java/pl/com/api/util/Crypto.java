package pl.com.api.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.bitcoinj.core.Base58;    // możesz dodać dependency bitcoinj-core

public final class Crypto {
    private Crypto() {}

    /** SHA-256 raw hash */
    public static byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /** Base58 (Bitcoin-style) encoding */
    public static String toBase58(byte[] data) {
        return Base58.encode(data);
    }

    /** Hex string (lower-case) */
    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16))
                    .append(Character.forDigit( b        & 0xF, 16));
        }
        return sb.toString();
    }
}
