package pl.com.api.dto;

import lombok.Data;

@Data
public class AccessRequest {
    public String fromAddress;
    public String toAddress;
    public String dataKey;
    public String dataValue; // Wartość dla GRANT, pusta dla REVOKE
    public String privateKeySeedBase64;
}
