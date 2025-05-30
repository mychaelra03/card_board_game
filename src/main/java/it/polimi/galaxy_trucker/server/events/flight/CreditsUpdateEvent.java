package it.polimi.galaxy_trucker.server.events.flight;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.ShipBoard;

import java.util.Set;

public class CreditsUpdateEvent extends EventModel {

    private final String playerName;
    private final int credits;

    public CreditsUpdateEvent(String playerName, int credits) {
        this.playerName = playerName;
        this.credits = credits;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getCredits() {
        return credits;
    }
}

