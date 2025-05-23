package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;

import java.awt.*;

public class CargoDoneEvent extends EventModel {

    private final String playerName;

    public CargoDoneEvent(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
}
