package pl.com.api.model;
import at.qubic.api.domain.std.response.TickInfo;
import lombok.Data;

@Data
public class TickInfoResponse {
    private TickInfo tickInfo;

    public TickInfo getTickInfo() {
        return tickInfo;
    }

    public void setTickInfo(TickInfo tickInfo) {
        this.tickInfo = tickInfo;
    }
}
