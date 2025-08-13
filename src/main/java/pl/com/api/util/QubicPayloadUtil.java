package pl.com.api.util;

import at.qubic.api.domain.std.SignedTransaction;
import at.qubic.api.domain.std.Transaction;
import at.qubic.api.domain.std.response.TickInfo;
import at.qubic.api.network.Node;
import at.qubic.api.service.TransactionService;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QubicPayloadUtil {

    /**
     * Zwraca wszystkie transakcje od addressA do addressB wraz z ich payloadem.
     */
//    public static List<SignedTransaction> getAllTxFromAtoB(
//            Node node,
//            String addressA,
//            String addressB
//    ) {
//        TransactionService txService = node.getTransactionService();
//
//        Flux<SignedTransaction> flux = txService.getTransactionsFrom(addressA);
//        List<SignedTransaction> txList = flux.collectList().block();
//        List<SignedTransaction> result = new ArrayList<>();
//        if (txList != null) {
//            for (SignedTransaction stx : txList) {
//                Transaction tx = stx.getTransaction();
//                if (addressB.equals(tx.getDestination())) {
//                    String payload = tx.getPayload() != null ? tx.getPayload() : "";
//                    System.out.println("TX " + tx.getHash()
//                            + " payload=" + payload
//                            + " tick=" + tx.getTick());
//                    result.add(stx);
//                }
//            }
//        }
//        return result;
//    }

    /**
     * Zwraca ostatnią transakcję od A do B z payloadem "med1" lub "med0".
     * Zwraca null, jeśli nie znaleziono żadnej takiej transakcji.
     */
//    public static SignedTransaction getLastMedTx(
//            Node node,
//            String addressA,
//            String addressB
//    ) {
//        List<SignedTransaction> all = getAllTxFromAtoB(node, addressA, addressB);
//        return all.stream()
//                .filter(stx -> {
//                    String payload = stx.getTransaction().getPayload();
//                    return "med1".equals(payload) || "med0".equals(payload);
//                })
//                .max(Comparator.comparingLong(stx -> stx.getTransaction().getTick()))
//                .orElse(null);
//    }
    /**
     * Tworzy payload z flagą uprawnienia ("med1" lub "med0"), ustawia inputSize i zwraca obiekt SignedTransaction.
     *
     * @param node         połączenie do węzła Qubic
     * @param sender       tożsamość nadawcy (Identity z dostępem do prywatnego klucza)
     * @param contractPub  klucz publiczny kontraktu docelowego
     * @param functionIndex indeks funkcji smart kontraktu (np. setPermission)
     * @param grant        true → med1 (nadanie), false → med0 (odmowa)
     * @return SignedTransaction gotowy do wysłania
     */
//    public static SignedTransaction createPermissionTx(
//            Node node,
//            Identity sender,
//            PublicKey contractPub,
//            int functionIndex,
//            boolean grant
//    ) {
//        // 1. Pobierz aktualny tick
//        TickInfo tickInfo = node.getCurrentTickInfo().block();
//        long tick = tickInfo.getTick();
//
//        // 2. Przygotuj payload: tekst "med1" lub "med0"
//        byte[] payload = (grant ? "med1" : "med0")
//                .getBytes(StandardCharsets.UTF_8);
//
//        // 3. Ustaw input size jako długość payload
//        int inputSize = payload.length;
//
//        // 4. Zbuduj transakcję wywołania smart kontraktu przez QubicJ
//        // Metoda createSmartContractCall powinna przyjmować tick, nadawcę, klucz kontraktu,
//        // indeks funkcji, inputSize i payload – zgodnie z API QubicJ.
//        SignedTransaction txn = node.getTransactionService()
//                .createSmartContractCall(tick, sender, contractPub, functionIndex, inputSize, payload);
//
//        return txn;
//    }

    /**
     * Wysyła transakcję do sieci i blokuje do zakończenia.
     */
//    public static void sendTransaction(Node node, SignedTransaction txn) {
//        node.broadcastTransaction(txn).block();
//    }

}
