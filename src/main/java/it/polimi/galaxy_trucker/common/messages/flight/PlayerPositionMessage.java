package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerPositionMessage implements GameMessage {

    private String playerName;
    private int oldPosition;
    private int newPosition;

    public PlayerPositionMessage() {}

    public PlayerPositionMessage(String playerName, int oldPosition, int newPosition) {
        this.playerName = playerName;
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getOldPosition() {
        return oldPosition;
    }
    public int getNewPosition() {
        return newPosition;
    }

    @Override
    public String getType() {
        return "playerPosition";
    }
}
