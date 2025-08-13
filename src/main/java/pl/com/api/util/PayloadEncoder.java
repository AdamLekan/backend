package pl.com.api.util;

public class PayloadEncoder {

    /**
     * Koduje 2-bajtowy payload dla uprawnienia MED0/MED1.
     * @param id opcjonalny identyfikator (0-255); jeżeli null, bajt [0] zostanie ustawiony na 0x00.
     * @param medFlag true jeśli MED1, false jeśli MED0.
     * @return tablica bajtów [bajt0, bajt1] zgodnie z formatem.
     */
    public static byte[] encodeMedPayload(Byte id, boolean medFlag) {
        byte[] payload = new byte[2];
        // Ustawiamy bajt 0:
        payload[0] = (id != null) ? id.byteValue() : (byte)0x00;
        // Ustawiamy bajt 1: 0x01 dla MED1, 0x00 dla MED0
        payload[1] = medFlag ? (byte)0x01 : (byte)0x00;
        return payload;
    }
}

