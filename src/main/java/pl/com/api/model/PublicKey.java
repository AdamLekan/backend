package pl.com.api.model;


public class PublicKey {
    private final byte[] bytes;

    public PublicKey(byte[] bytes) {
        if (bytes == null || bytes.length != 32) {
            throw new IllegalArgumentException("Klucz publiczny musi składać się z 32 bajtów.");
        }
        this.bytes = bytes;
    }

    public PublicKey(String base32Address) {
        if (base32Address == null) {
            throw new IllegalArgumentException("Adres Base32 nie może być pusty.");
        }
        this.bytes = decodeBase32Address(base32Address);
    }

    public byte[] getBytes() {
        return bytes;
    }

    private static byte[] decodeBase32Address(String base32) {
        String base32chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        base32 = base32.toUpperCase().replaceAll("=", "");

        int numBytes = base32.length() * 5 / 8;
        byte[] result = new byte[numBytes];

        int buffer = 0;
        int bitsLeft = 0;
        int count = 0;

        for (char c : base32.toCharArray()) {
            int val = base32chars.indexOf(c);
            if (val < 0) throw new IllegalArgumentException("Nieprawidłowy znak w adresie Base32: " + c);

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
