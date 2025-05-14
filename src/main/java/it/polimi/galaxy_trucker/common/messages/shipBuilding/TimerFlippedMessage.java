package it.polimi.galaxy_trucker.common.messages.shipBuilding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimerFlippedMessage implements GameMessage {

    public TimerFlippedMessage() {}

    @Override
    public String getType() {
        return "timerFlipped";
    }
}
