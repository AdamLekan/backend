//package pl.com.api.controller;
//
//
//
//import java.net.http.*;
//import java.net.URI;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import com.fasterxml.jackson.databind.*;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//
//public class QubicHttpTransfer {
//    public static void main(String[] args) throws Exception {
//        HttpClient client = HttpClient.newHttpClient();
//        ObjectMapper M = new ObjectMapper();
//
//        // 1) Pobranie tick-info
//        HttpRequest tickReq = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8000/tick-info"))
//                .build();
//        String tickResp = client.send(tickReq, HttpResponse.BodyHandlers.ofString()).body();
//        long tick = M.readTree(tickResp).path("tickInfo").path("tick").asLong();
//
//        // 2) Przygotowanie payload (uprawnienia -> Base64)
//        String permissionsJson = "{\"from\":\"A\",\"to\":\"B\",\"rights\":[\"read\",\"write\"]}";
//        String payloadB64 = Base64.getEncoder()
//                .encodeToString(permissionsJson.getBytes(StandardCharsets.UTF_8));
//
//        // 3) Budowa transakcji (bez podpisu)
//        ObjectNode tx = M.createObjectNode();
//        tx.put("sender",    "<publicKeyA>");
//        tx.put("recipient", "<addressB>");
//        tx.put("amount",     0);
//        tx.put("tick",       tick);
//        tx.put("payload",    payloadB64);
//        byte[] txBytes = M.writeValueAsBytes(tx);
//
//        // 4) Podpis (tu uproszczony; stosujesz własną funkcję sign())
//        byte[] signature = MyCrypto.sign(txBytes, hexPrivateKeyA);
//        byte[] signedTxBytes = combine(txBytes, signature);
//        String encodedTx = Base64.getEncoder().encodeToString(signedTxBytes);
//
//        // 5) Wysłanie
//        ObjectNode body = M.createObjectNode();
//        body.put("encodedTransaction", encodedTx);
//        HttpRequest sendReq = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8000/broadcast-transaction"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
//                .build();
//        String sendResp = client.send(sendReq, HttpResponse.BodyHandlers.ofString()).body();
//        System.out.println("Odpowiedź sieci: " + sendResp);
//    }
//
//    // Łączy transakcję i podpis w jeden bajtowy strumień
//    private static byte[] combine(byte[] a, byte[] b) {
//        byte[] c = new byte[a.length + b.length];
//        System.arraycopy(a, 0, c, 0, a.length);
//        System.arraycopy(b, 0, c, a.length, b.length);
//        return c;
//    }
//}
