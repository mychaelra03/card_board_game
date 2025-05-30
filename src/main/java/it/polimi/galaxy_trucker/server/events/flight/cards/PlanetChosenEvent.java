package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;

public class PlanetChosenEvent extends EventModel {

    private final String playerName;
    private final int chosenPlanet;

    public PlanetChosenEvent(String playerName, int chosenPlanet) {
        this.playerName = playerName;
        this.chosenPlanet = chosenPlanet;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getChosenPlanet() {
        return chosenPlanet;
    }
}
