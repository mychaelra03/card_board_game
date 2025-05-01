package it.polimi.galaxy_trucker.Common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoardSelectMessage implements GameMessage {
    public enum Action {
        PREVIOUS, NEXT
    }
    private Action action;

    public BoardSelectMessage() {}

    public BoardSelectMessage(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String getType() {
        return "boardSelect";
    }
}
