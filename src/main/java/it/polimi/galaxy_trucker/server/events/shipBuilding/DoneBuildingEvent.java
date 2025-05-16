package it.polimi.galaxy_trucker.server.events.shipBuilding;

import it.polimi.galaxy_trucker.server.events.EventModel;

public class DoneBuildingEvent extends EventModel {
    private final String playerName;
    private final int position;

    public DoneBuildingEvent(String playerName, int position) {
        this.playerName = playerName;
        this.position = position;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPosition() {
        return position;
    }
}

