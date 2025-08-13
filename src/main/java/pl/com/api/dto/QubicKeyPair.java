package pl.com.api.dto;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class QubicKeyPair {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Lob
    private byte[] privateKey;

    @Lob
    private byte[] publicKey;

    @Column(length = 64, unique = true)
    private String addressId;

    // Gettery i Settery
    public UUID getId() { return id; }
    public byte[] getPrivateKey() { return privateKey; }
    public void setPrivateKey(byte[] privateKey) { this.privateKey = privateKey; }
    public byte[] getPublicKey() { return publicKey; }
    public void setPublicKey(byte[] publicKey) { this.publicKey = publicKey; }
    public String getAddressId() { return addressId; }
    public void setAddressId(String addressId) { this.addressId = addressId; }
}
