package pl.com.api.model;

import lombok.Data;

@Data
public class BroadcastTransactionRequest {

    public String getEncodedTransaction() {
        return encodedTransaction;
    }

    private String encodedTransaction;

    public void setEncodedTransaction(String encodedTransaction) {
        this.encodedTransaction = encodedTransaction;
    }

    public BroadcastTransactionRequest() {
    }

    public BroadcastTransactionRequest(String encodedTransaction) {
        this.encodedTransaction = encodedTransaction;
    }


}
