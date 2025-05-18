package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DrawCardMessage implements GameMessage {

    public DrawCardMessage() {}

    @Override
    public String getType() {
        return "drawCard";
    }
}
