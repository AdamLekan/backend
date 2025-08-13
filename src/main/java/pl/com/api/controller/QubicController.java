package pl.com.api.controller;

import at.qubic.api.domain.std.response.TickInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.com.api.dto.TickInfoDto;
import pl.com.api.model.BroadcastResponse;
import pl.com.api.dto.WalletDto;
import pl.com.api.service.QubicService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/qubic")
public class QubicController {

    private final QubicService qubicService;

    public QubicController(QubicService qubicService) {
        this.qubicService = qubicService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerWallet(@RequestBody WalletDto wallet) {
        // Można dodać zapis do bazy danych
        System.out.println("Zarejestrowano wallet: " + wallet.getSeed());
        return ResponseEntity.ok("Zarejestrowano");
    }

    @PostMapping("/create-wallet")
    public ResponseEntity<String> createWallet(@RequestBody WalletDto wallet) {
        System.out.println("create-wallet: " + wallet.getSeed());
        boolean created = qubicService.createWalletInQubic(wallet);
        return created
                ? ResponseEntity.ok("Konto utworzone w Qubic")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Błąd tworzenia konta");
    }

    @GetMapping("/balance/{address}")
    public ResponseEntity<String> getBalance(@PathVariable String address) {
        String balance = qubicService.getQubicBalance(address);
        return ResponseEntity.ok(balance);
    }

//    @PostMapping("/send")
//    public ResponseEntity<String> sendTransaction(@Valid @RequestBody TransactionDto transaction) {
//        try {
//            // Dodajemy logowanie
//            System.out.println("Próba wysłania transakcji: " +
//                "z: " + transaction.getFrom() +
//                ", do: " + transaction.getTo() +
//                ", kwota: " + transaction.getAmount());
//
//            boolean success = qubicService.sendQubicTransaction(
//                transaction.getFrom(),
//                transaction.getTo(),
//                transaction.getAmount()
//            );
//
//            if (success) {
//                return ResponseEntity.ok("Transakcja została wysłana pomyślnie");
//            } else {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Wystąpił błąd podczas wysyłania transakcji");
//            }
//        } catch (Exception e) {
//            System.err.println("Błąd podczas przetwarzania transakcji: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body("Błąd: " + e.getMessage());
//        }
//    }

    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcast(@RequestBody String signedTransaction) throws JsonProcessingException {
        BroadcastResponse result = qubicService.broadcastTransaction(signedTransaction);
        return ResponseEntity.ok(result.getStatus());
    }

    @PostMapping("/last_tick")
    public ResponseEntity<String> latestTick() {
        System.out.println("Pobieranie ostatniego ticku...");
        long latestTick = qubicService.getLatestTick();
        System.out.println("Aktualny tick: " + latestTick);
    return ResponseEntity.ok(String.valueOf(latestTick));
    }

//    @PostMapping("/send")
//    public String sendTransaction(@RequestBody SendRequest request) throws Exception {
//        byte[] senderPubKey = Base64.getDecoder().decode(request.senderPublicKey);
//        byte[] recipientId = Base64.getDecoder().decode(request.recipientId);
//        byte[] senderPrivateKey = Base64.getDecoder().decode(request.senderPrivateKey);
//
//        return transactionSender.sendValueTransaction(senderPubKey, recipientId, senderPrivateKey, request.amount);
//    }


    @GetMapping("/tick-info")
    public Map<String, TickInfoDto> getTickInfo() {
        TickInfo tickInfo = qubicService.getCurrentTickInfo();
        // Mapujemy na DTO lub zwracamy bezpośrednio, w zależności od potrzeb.
        TickInfoDto dto = new TickInfoDto(tickInfo.getTick(), tickInfo.getTickDuration(),
                tickInfo.getEpoch(), tickInfo.getInitialTick());
        // Zwracamy zewnętrzny obiekt JSON z polem "tickInfo" (dla zgodności z Qubic HTTP API)
        return Collections.singletonMap("tickInfo", dto);
    }

    @PostMapping("/broadcast-transaction")
    public ResponseEntity<BroadcastResponse> broadcastTransaction(
            @RequestParam("med") String medParam,
            @RequestParam(value="id", required=false) Integer idParam) {
        // Walidacja parametru 'med'
        boolean medFlag;
        if ("MED1".equalsIgnoreCase(medParam) || "1".equals(medParam)) {
            medFlag = true;
        } else if ("MED0".equalsIgnoreCase(medParam) || "0".equals(medParam)) {
            medFlag = false;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid med parameter. Use MED0 or MED1.");
        }
        // Konwersja identyfikatora (Integer -> Byte), jeśli podany
        Byte idByte = (idParam != null) ? idParam.byteValue() : null;

        // Wywołanie warstwy serwisowej
        BroadcastResponse result = null;//qubicService.broadcastPermission(idByte, medFlag);
        return ResponseEntity.ok(result);
    }
}