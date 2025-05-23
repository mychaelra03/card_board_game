package it.polimi.galaxy_trucker.server.events.flight;

import it.polimi.galaxy_trucker.server.events.EventModel;

public class PlayerPositionUpdateEvent extends EventModel {

    private final String playerName;
    private final int newPosition;

    public PlayerPositionUpdateEvent(String playerName, int newPosition) {
        this.playerName = playerName;
        this.newPosition = newPosition;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getNewPosition() {
        return newPosition;
    }
}

