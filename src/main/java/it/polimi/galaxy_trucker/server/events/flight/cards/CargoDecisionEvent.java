package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.Player;

import java.util.Set;

public class CargoDecisionEvent extends EventModel {

    private final Set<Player> players;

    public CargoDecisionEvent(Set<Player> players) {
        this.players = players;
    }

    public Set<Player> getPlayers() {
        return players;
    }
}
