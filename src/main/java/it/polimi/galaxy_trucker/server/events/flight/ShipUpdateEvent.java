package it.polimi.galaxy_trucker.server.events.flight;

import it.polimi.galaxy_trucker.common.messages.flight.TileInfo;
import it.polimi.galaxy_trucker.server.events.EventModel;

import java.util.Set;

public class ShipUpdateEvent extends EventModel {

    private final String playerName;
    private final Set<TileInfo> tiles;

    public ShipUpdateEvent(String playerName, Set<TileInfo> tiles) {
        this.playerName = playerName;
        this.tiles = tiles;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Set<TileInfo> getTiles() {
        return tiles;
    }
}

