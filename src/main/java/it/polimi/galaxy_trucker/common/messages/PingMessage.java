package it.polimi.galaxy_trucker.common.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PingMessage implements GameMessage {

    public PingMessage() {}

    @Override
    public String getType() {
        return "ping";
    }
}
