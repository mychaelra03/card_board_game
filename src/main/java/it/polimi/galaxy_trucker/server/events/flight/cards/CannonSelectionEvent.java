package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.Player;

public class CannonSelectionEvent extends EventModel {

    private final Player player;

    public CannonSelectionEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
