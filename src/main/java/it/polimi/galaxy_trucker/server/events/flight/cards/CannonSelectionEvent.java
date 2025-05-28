package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.ShipBoard;

import java.awt.*;
import java.util.Set;

public class CannonSelectionEvent extends EventModel {

    private final Player player;

    public CannonSelectionEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
