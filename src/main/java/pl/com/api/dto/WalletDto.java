package pl.com.api.dto;

public class WalletDto {
    private String publicKey;
    private String privateKey;
    private String seed;

    public WalletDto() {}

    public WalletDto(String publicKey, String privateKey, String seed) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.seed = seed;
    }
    public WalletDto(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    // gettery i settery
}
