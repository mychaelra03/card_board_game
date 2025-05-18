package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;
import it.polimi.galaxy_trucker.server.model.ShipBoard;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CrewMessage implements GameMessage {

    private String playerName;
    private List<ShipBoard.CrewData> crew;

    public CrewMessage() {}

    public CrewMessage(String playerName, List<ShipBoard.CrewData> crew) {
        this.playerName = playerName;
        this.crew = crew;
    }

    public String getPlayerName() {
        return playerName;
    }

    public List<ShipBoard.CrewData> getCrew() {
        return crew;
    }

    @Override
    public String getType() {
        return "crew";
    }
}
