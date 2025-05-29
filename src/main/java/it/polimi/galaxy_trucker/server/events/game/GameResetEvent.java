package it.polimi.galaxy_trucker.server.events.game;

import it.polimi.galaxy_trucker.server.events.EventModel;

public class GameResetEvent extends EventModel {
    private final String playerName;

    public GameResetEvent(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
}
