package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.Player;

import java.awt.*;

public class CargoUnloadedEvent extends EventModel {

    private final Player player;
    private final Point tileCoords;
    private final int containerIndex;

    public CargoUnloadedEvent(Player player, Point tileCoords, int containerIndex) {
        this.player = player;
        this.tileCoords = tileCoords;
        this.containerIndex = containerIndex;
    }

    public Player getPlayer() {
        return player;
    }
    public Point getTileCoords() {
        return tileCoords;
    }
    public int getContainerIndex() {
        return containerIndex;
    }
}
