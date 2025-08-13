package pl.com.api.service;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class QubicAccessService {

    private static final String API_BASE = "https://testnet-rpc.qubic.org/api/v1";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void sendAccessRequest(String from, String privateKey, String to, String dataHash) throws Exception {
        String message = String.format("REQUEST_ACCESS|%s|%s|%s", from, to, dataHash);
        sendMessage(from, privateKey, "access_requests", message);
    }

    public void sendAccessResponse(String from, String privateKey, String to, String dataHash, boolean grant) throws Exception {
        String type = grant ? "GRANT_ACCESS" : "DENY_ACCESS";
        String message = String.format("%s|%s|%s|%s", type, from, to, dataHash);
        sendMessage(from, privateKey, "access_responses", message);
    }

    public List<String> readChannelMessages(String channel) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + "/channel/" + channel))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray jsonArray = new JSONArray(response.body());

        return jsonArray.toList().stream()
                .map(obj -> ((java.util.Map<?, ?>) obj).get("message").toString())
                .collect(Collectors.toList());
    }

    private void sendMessage(String from, String privateKey, String channel, String message) throws Exception {
        JSONObject json = new JSONObject();
        json.put("fromAddress", from);
        json.put("key", privateKey);
        json.put("channel", channel);
        json.put("message", message);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE + "/message/send"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 300) {
            throw new RuntimeException("Qubic message send failed: " + response.body());
        }
    }
}
