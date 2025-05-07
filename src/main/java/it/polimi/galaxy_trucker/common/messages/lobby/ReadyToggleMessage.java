package it.polimi.galaxy_trucker.common.messages.lobby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReadyToggleMessage implements GameMessage {

    public ReadyToggleMessage() {}

    @Override
    public String getType() {
        return "readyToggle";
    }
}
