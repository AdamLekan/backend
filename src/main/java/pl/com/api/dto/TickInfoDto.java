package pl.com.api.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TickInfoDto {

    private int tick;
    private short tickDuration;
    private  short epoch;
    private  int initialTick;

    public TickInfoDto(int tick, short tickDuration, short epoch, int initialTick) {
        this.tick = tick;
        this.tickDuration = tickDuration;
        this.epoch = epoch;
        this.initialTick = initialTick;
    }


}
