package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;

public class PlayerStatusEvent extends EventModel {

    public enum Status {
        PUNISHED, REWARDED, SKIPPED_CREW, SKIPPED_ENEMY, WORST_PLAYER
    }

    private final String playerName;
    private final Status status;

    public PlayerStatusEvent(String playerName, Status status) {
        this.playerName = playerName;
        this.status = status;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Status getStatus() {
        return status;
    }
}
