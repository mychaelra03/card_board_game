package it.polimi.galaxy_trucker.server.events.flight;

import it.polimi.galaxy_trucker.common.messages.flight.TileInfo;
import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.ShipBoard;

import java.util.Set;

public class CrewUpdateEvent extends EventModel {

    private final String playerName;
    private final Set<ShipBoard.CrewData> crew;

    public CrewUpdateEvent(String playerName, Set<ShipBoard.CrewData> crew) {
        this.playerName = playerName;
        this.crew = crew;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Set<ShipBoard.CrewData> getCrew() {
        return crew;
    }
}

