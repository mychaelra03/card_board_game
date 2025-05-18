package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

import java.awt.*;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InvalidShipMessage implements GameMessage {

    private String playerName;
    private Set<Point> invalidTiles;

    public InvalidShipMessage() {}

    public InvalidShipMessage(String playerName, Set<Point> invalidTiles) {
        this.playerName = playerName;
        this.invalidTiles = invalidTiles;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Set<Point> getInvalidTiles() {
        return invalidTiles;
    }

    @Override
    public String getType() {
        return "invalidShip";
    }
}
