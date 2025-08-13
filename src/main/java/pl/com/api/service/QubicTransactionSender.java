package pl.com.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.ByteBuffer;
import java.util.Base64;

@Service
public class QubicTransactionSender {

    private final String qubicApiUrl = "https://rpc-staging.qubic.org";
    private final RestTemplate restTemplate = new RestTemplate();
    private final QubicService tickService;

    public QubicTransactionSender(QubicService tickService) {
        this.tickService = tickService;
    }

    public String sendTransaction(byte[] senderPubKey, byte[] recipientId, byte[] senderPrivateKey, long value, boolean isGrant) throws Exception {
        long fee = 1000L;
        long tick = tickService.getLatestTick();

        byte[] payload;
        if (isGrant) {
            payload = QubicTransactionBuilder.buildGrantTransaction(senderPubKey, recipientId, value, fee, tick);
        } else {
            payload = QubicTransactionBuilder.buildValueTransaction(senderPubKey, recipientId, value, fee, tick);
        }

        byte[] signature = QubicTransactionSigner.sign(senderPrivateKey, payload);

        ByteBuffer txBuffer = ByteBuffer.allocate(payload.length + signature.length + senderPubKey.length);
        txBuffer.put(payload);
        txBuffer.put(signature);
        txBuffer.put(senderPubKey);

        String signedTransaction = Base64.getEncoder().encodeToString(txBuffer.array());
        return broadcastTransaction(signedTransaction);
    }

    private String broadcastTransaction(String signedTransaction) {
        String url = qubicApiUrl + "/broadcast-transaction";
        var requestBody = new BroadcastRequest(signedTransaction);
        return restTemplate.postForObject(url, requestBody, String.class);
    }

    private static class BroadcastRequest {
        public String signedTransaction;
        public BroadcastRequest(String signedTransaction) { this.signedTransaction = signedTransaction; }
    }
}
