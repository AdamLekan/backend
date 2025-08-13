package pl.com.api.model;


import pl.com.api.util.Crypto;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Reprezentuje podpisaną transakcję w sieci Qubic:
 * • budowę (pola)
 * • serializację do bajtów
 * • obliczanie ID
 */
public class SignedTransaction {
    // wersja protokołu (1 bajt)
    private final byte protocolVersion = 0x01;

    // klucz publiczny nadawcy (32 bajty)
    private final byte[] senderPubKey;

    // klucz publiczny odbiorcy (32 bajty)
    private final byte[] recipientPubKey;

    // ilość tokenów
    private final long amount;

    // sieciowy tick (timestamp)
    private final long tick;

    // opcjonalny payload (np. JSON uprawnień)
    private final byte[] payload;

    // podpis (64 bajty dla Ed25519)
    private final byte[] signature;

    // --- Konstruktor ukryty, używaj TransactionBuildera ---
    SignedTransaction(byte[] senderPubKey,
                      byte[] recipientPubKey,
                      long amount,
                      long tick,
                      byte[] payload,
                      byte[] signature) {
        this.senderPubKey    = senderPubKey;
        this.recipientPubKey = recipientPubKey;
        this.amount          = amount;
        this.tick            = tick;
        this.payload         = payload != null ? payload : new byte[0];
        this.signature       = signature;
    }

    /**
     * Serializuje wszystkie pola do surowego formatu bajtów:
     * [version][len(sender)] [senderPubKey]
     * [len(recipient)] [recipientPubKey]
     * [8B amount][8B tick]
     * [len(payload)] [payload]
     * [len(signature)] [signature]
     */
    public byte[] toBytes() {
        // obliczamy rozmiar bufora
        int size = 1
                + varIntSize(senderPubKey.length) + senderPubKey.length
                + varIntSize(recipientPubKey.length) + recipientPubKey.length
                + 8  // amount
                + 8  // tick
                + varIntSize(payload.length) + payload.length
                + varIntSize(signature.length) + signature.length;

        ByteBuffer buf = ByteBuffer.allocate(size);
        buf.put(protocolVersion);

        putVarInt(buf, senderPubKey.length);
        buf.put(senderPubKey);

        putVarInt(buf, recipientPubKey.length);
        buf.put(recipientPubKey);

        buf.putLong(amount);
        buf.putLong(tick);

        putVarInt(buf, payload.length);
        buf.put(payload);

        putVarInt(buf, signature.length);
        buf.put(signature);

        return buf.array();
    }

    /** Identyfikator transakcji: hash(surowe bajty) */
    public String getId() {
        byte[] raw = toBytes();
        // np. SHA-256 → hex lub Base58 (wg specy)
        byte[] hash = Crypto.sha256(raw);
        return Crypto.toBase58(hash);
    }

    /** Wielkość VarInt (tak jak w Protobuf) dla n bajtów */
    private int varIntSize(int n) {
        if (n < 0x80) return 1;
        else if (n < 0x4000) return 2;
        else if (n < 0x200000) return 3;
        else if (n < 0x10000000) return 4;
        else return 5;
    }

    /** Zapisuje VarInt do buf */
    private void putVarInt(ByteBuffer buf, int value) {
        while ((value & ~0x7F) != 0) {
            buf.put((byte)((value & 0x7F) | 0x80));
            value >>>= 7;
        }
        buf.put((byte)value);
    }

    // --- Gettery, equals, hashCode, toString() itd. poniżej ---
    public byte[] getSenderPubKey()    { return senderPubKey;    }
    public byte[] getRecipientPubKey() { return recipientPubKey; }
    public long   getAmount()          { return amount;          }
    public long   getTick()            { return tick;            }
    public byte[] getPayload()         { return payload;         }
    public byte[] getSignature()       { return signature;       }

    @Override
    public String toString() {
        return "SignedTransaction{" +
                "sender=" + Crypto.toBase58(senderPubKey) +
                ", recipient=" + Crypto.toBase58(recipientPubKey) +
                ", amount=" + amount +
                ", tick=" + tick +
                ", payloadLen=" + payload.length +
                ", signature(hex)=" + Crypto.toHex(Arrays.copyOf(signature, 8)) + "..." +
                '}';
    }

    // === Clase metody do deserializacji, walidacji podpisu itd. można dodać ===
}
