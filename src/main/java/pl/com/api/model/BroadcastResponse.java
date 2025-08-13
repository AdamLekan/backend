package pl.com.api.model;

import lombok.Data;

@Data
public class BroadcastResponse {
    private String transactionId;
    private String status;

    public BroadcastResponse() {
    }

    public BroadcastResponse(String transactionId, String status) {
        this.transactionId = transactionId;
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BroadcastResponse{" +
                "transactionId='" + transactionId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
