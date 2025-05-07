package it.polimi.galaxy_trucker.common.messages.lobby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KickedMessage implements GameMessage {

    public KickedMessage() {}

    @Override
    public String getType() {
        return "kicked";
    }
}
