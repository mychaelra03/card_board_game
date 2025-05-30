package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.ShipBoard;

import java.util.Set;

public class CrewDecisionEvent extends EventModel {

    private final String playerName;
    private final Set<ShipBoard.CrewData> crewTiles;

    public CrewDecisionEvent(String playerName, Set<ShipBoard.CrewData> crewTiles) {
        this.playerName = playerName;
        this.crewTiles = crewTiles;
    }

    public String getPlayerName() {
        return playerName;
    }
    public Set<ShipBoard.CrewData> getCrewTiles() {
        return crewTiles;
    }
}
