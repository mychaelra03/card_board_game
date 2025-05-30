package it.polimi.galaxy_trucker.common.messages.shipBuilding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimerStartMessage implements GameMessage {

    private int duration;
    private int timesFlipped;

    public TimerStartMessage() {}

    public TimerStartMessage(int duration, int timesFlipped) {
       this.duration = duration;
       this.timesFlipped = timesFlipped;
    }

    public int getDuration() {
       return duration;
    }

    public int getTimesFlipped() {
        return timesFlipped;
    }

    @Override
    public String getType() {
        return "timer_start";
    }
}
