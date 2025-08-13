package pl.com.api.model;

import lombok.Data;

@Data
public class BroadcastRequest {
    // Jeśli true to MED1 (0x01), jeśli false to MED0 (0x00)
    private boolean med1;

    public boolean isMed1() {
        return med1;
    }

    public void setMed1(boolean med1) {
        this.med1 = med1;
    }
}