package pl.com.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.api.model.BroadcastRequest;
import pl.com.api.service.QubicService;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/broadcast")
public class BroadcastController {
    private final QubicService qubicService;

    public BroadcastController(QubicService qubicService) {
        this.qubicService = qubicService;
    }

    @PostMapping
    public ResponseEntity<String> broadcast(@RequestBody BroadcastRequest request) throws Exception {
        String txId = String.valueOf(qubicService.broadcastTransaction(request.isMed1()));
        return ResponseEntity.ok("Transakcja wysłana, ID: " + txId);
    }
}