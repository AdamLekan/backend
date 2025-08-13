package pl.com.api.service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class QubicTransactionBuilder {

    public static byte[] buildValueTransaction(byte[] senderPubKey, byte[] recipientId, long amount, long fee, long tick) {
        ByteBuffer buffer = ByteBuffer.allocate(1 + 32 + 32 + 8 + 8 + 8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x00); // VALUE
        buffer.put(senderPubKey);
        buffer.put(recipientId);
        buffer.putLong(amount);
        buffer.putLong(fee);
        buffer.putLong(tick);
        return buffer.array();
    }

    public static byte[] buildGrantTransaction(byte[] senderPubKey, byte[] recipientId, long permission, long fee, long tick) {
        ByteBuffer buffer = ByteBuffer.allocate(1 + 32 + 32 + 8 + 8 + 8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) 0x01); // GRANT
        buffer.put(senderPubKey);
        buffer.put(recipientId);
        buffer.putLong(permission);
        buffer.putLong(fee);
        buffer.putLong(tick);
        return buffer.array();
    }
}
