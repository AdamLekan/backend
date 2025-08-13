package pl.com.api.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

public class PermissionPayload {
    private final byte[] targetPublicKey;
    private final int permissionType;

    public PermissionPayload(String targetPublicKeyBase64, int permissionType) {
        this.targetPublicKey = Base64.getDecoder().decode(targetPublicKeyBase64);
        if (this.targetPublicKey.length != 32) {
            throw new IllegalArgumentException("Klucz publiczny w payloadzie musi składać się z 32 bajtów.");
        }
        this.permissionType = permissionType;
    }

    public int getSize() {
        return 32 + 4;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(getSize());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(this.targetPublicKey);
        buffer.putInt(this.permissionType);
        return buffer.array();
    }
}