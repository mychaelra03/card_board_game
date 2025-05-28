package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.Player;

public class DiceThrowEvent extends EventModel {

    private final String playerName;
    private final int[] dice;

    public DiceThrowEvent(String playerName, int[] dice) {
        this.playerName = playerName;
        this.dice = dice;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int[] getDice() {
        return dice;
    }
}
