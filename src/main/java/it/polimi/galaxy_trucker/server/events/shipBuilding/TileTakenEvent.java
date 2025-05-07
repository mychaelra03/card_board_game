package it.polimi.galaxy_trucker.server.events.shipBuilding;

import it.polimi.galaxy_trucker.server.events.EventModel;

public class TileTakenEvent extends EventModel {
    private final String playerName;
    private final int tileId;

    public TileTakenEvent(String playerName, int tileId) {
        this.playerName = playerName;
        this.tileId = tileId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getTileId() {
        return tileId;
    }
}

