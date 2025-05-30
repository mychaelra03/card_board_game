package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.Player;

import java.awt.*;
import java.util.Set;

public class CargoSwappedEvent extends EventModel {

    private final Player player;
    private final int blockIndex;
    private final Point tileCoords;
    private final int containerIndex;

    public CargoSwappedEvent(Player player, int blockIndex, Point tileCoords, int containerIndex) {
        this.player = player;
        this.blockIndex = blockIndex;
        this.tileCoords = tileCoords;
        this.containerIndex = containerIndex;
    }

    public Player getPlayer() {
        return player;
    }
    public int getBlockIndex() {
        return blockIndex;
    }
    public Point getTileCoords() {
        return tileCoords;
    }
    public int getContainerIndex() {
        return containerIndex;
    }
}
