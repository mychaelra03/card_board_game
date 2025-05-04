package it.polimi.galaxy_trucker.common.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KickedMessage implements GameMessage {

    public KickedMessage() {}

    @Override
    public String getType() {
        return "kicked";
    }
}
