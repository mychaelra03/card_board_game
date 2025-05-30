package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;
import it.polimi.galaxy_trucker.server.model.ShipBoard;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CrewMessage implements GameMessage {

    private String type;

    private String playerName;
    private Set<ShipBoard.CrewData> crew;

    public CrewMessage() {}

    public CrewMessage(String type, String playerName, Set<ShipBoard.CrewData> crew) {
        this.type = type;
        this.playerName = playerName;
        this.crew = crew;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Set<ShipBoard.CrewData> getCrew() {
        return crew;
    }

    @Override
    public String getType() {
        return type;
    }
}
