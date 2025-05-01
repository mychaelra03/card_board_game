package it.polimi.galaxy_trucker.Common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReadyToggleMessage implements GameMessage {

    public ReadyToggleMessage() {}

    @Override
    public String getType() {
        return "readyToggle";
    }
}
