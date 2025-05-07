package it.polimi.galaxy_trucker.server.events.shipBuilding;

import it.polimi.galaxy_trucker.server.events.EventModel;

public class CardsTakenEvent extends EventModel {
    private final String playerName;
    private final int pileId;

    public CardsTakenEvent(String playerName, int pileId) {
        this.playerName = playerName;
        this.pileId = pileId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPileId() {
        return pileId;
    }
}

