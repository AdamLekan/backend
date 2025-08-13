package pl.com.api.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

public class QubicTransaction {

    private PublicKey sourcePublicKey;
    private PublicKey destinationPublicKey;
    private long tick;
    private int inputSize;
    private int inputType;
    private long amount;
    private byte[] payload;
    private byte[] signature;

    public QubicTransaction setSourcePublicKey(PublicKey key) {
        this.sourcePublicKey = key;
        return this;
    }

    public QubicTransaction setDestinationPublicKey(PublicKey key) {
        this.destinationPublicKey = key;
        return this;
    }

    public QubicTransaction setTick(long tick) {
        this.tick = tick;
        return this;
    }

    public QubicTransaction setInputSize(int size) {
        this.inputSize = size;
        return this;
    }

    public QubicTransaction setInputType(int type) {
        this.inputType = type;
        return this;
    }

    public QubicTransaction setAmount(long amount) {
        this.amount = amount;
        return this;
    }

    public QubicTransaction setPayload(byte[] payload) {
        this.payload = payload;
        return this;
    }

    public void build(String seed) {
        // Tu podpisywanie transakcji
        // Można użyć SHA256 + ed25519 itp. (symulacja na razie)
    }

    public byte[] getPackageData() {
        // Tutaj zwróć dane binarne do zakodowania w base64
        // W prawdziwym SDK to byłby zestaw wszystkich danych + podpis
        ByteBuffer buffer = ByteBuffer.allocate(4 + inputSize + 32); // przykładowo
        buffer.put(payload); // uproszczenie — tylko payload
        return buffer.array();
    }

//    public String encodeTransactionToBase64() {
//        return Base64.getEncoder().encodeToString(getPackageData());
//    }

    /**
     * Ustawia 64-bajtowy podpis Ed25519 dla transakcji.
     */
    public void setSignature(byte[] signature) {
        if (signature == null || signature.length != 64) {
            throw new IllegalArgumentException("Podpis musi składać się z 64 bajtów.");
        }
        this.signature = signature;
    }

    /**
     * Generuje skrót (digest) transakcji, czyli dane, które należy podpisać.
     * @return Tablica bajtów reprezentująca skrót transakcji.
     */
    public byte[] getDigest() {
        if (sourcePublicKey == null || destinationPublicKey == null || payload == null) {
            throw new IllegalStateException("Nie można wygenerować skrótu: wymagane pola nie są ustawione.");
        }

        // Transakcje Qubic używają porządku bajtów Little-Endian.
        int bufferSize = sourcePublicKey.getBytes().length +
                destinationPublicKey.getBytes().length +
                8 + // amount (long)
                8 + // tick (long)
                4 + // inputType (int)
                4 + // inputSize (int)
                payload.length;

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.put(sourcePublicKey.getBytes());
        buffer.put(destinationPublicKey.getBytes());
        buffer.putLong(amount);
        buffer.putLong(tick);
        buffer.putInt(inputType);
        buffer.putInt(inputSize);
        buffer.put(payload);

        return buffer.array();
    }

    /**
     * Łączy skrót transakcji z jej podpisem, a następnie koduje wynik do formatu Base64.
     * @return Zakodowany w Base64 ciąg znaków gotowy do wysłania do sieci Qubic.
     */
    public String encodeTransactionToBase64() {
        if (signature == null) {
            throw new IllegalStateException("Transakcja nie jest podpisana. Użyj najpierw metody setSignature().");
        }
        byte[] digest = getDigest();
        byte[] signedTx = new byte[digest.length + signature.length];

        System.arraycopy(digest, 0, signedTx, 0, digest.length);
        System.arraycopy(signature, 0, signedTx, digest.length, signature.length);

        return Base64.getEncoder().encodeToString(signedTx);
    }

}