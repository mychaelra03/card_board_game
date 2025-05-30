package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.Player;

public class PlanetSelectionEvent extends EventModel {

    private final String playerName;
    private final Player[] planetChosenBy;

    public PlanetSelectionEvent(String playerName, Player[] planetChosenBy) {
        this.playerName = playerName;
        this.planetChosenBy = planetChosenBy;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Player[] getPlanetChosenBy() {
        return planetChosenBy;
    }
}
