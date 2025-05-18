package it.polimi.galaxy_trucker.server.events.flight;

import it.polimi.galaxy_trucker.common.messages.flight.TileInfo;
import it.polimi.galaxy_trucker.server.events.EventModel;

import java.util.List;

public class ShipUpdateEvent extends EventModel {

    private final String playerName;
    private final List<TileInfo> tiles;

    public ShipUpdateEvent(String playerName, List<TileInfo> tiles) {
        this.playerName = playerName;
        this.tiles = tiles;
    }

    public String getPlayerName() {
        return playerName;
    }

    public List<TileInfo> getTiles() {
        return tiles;
    }
}

