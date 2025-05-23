package it.polimi.galaxy_trucker.server.events.flight;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.ShipBoard;

import java.util.Set;

public class BatteriesUpdateEvent extends EventModel {

    private final String playerName;
    private final Set<ShipBoard.BatteriesData> batteries;

    public BatteriesUpdateEvent(String playerName, Set<ShipBoard.BatteriesData> batteries) {
        this.playerName = playerName;
        this.batteries = batteries;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Set<ShipBoard.BatteriesData> getBatteries() {
        return batteries;
    }
}

