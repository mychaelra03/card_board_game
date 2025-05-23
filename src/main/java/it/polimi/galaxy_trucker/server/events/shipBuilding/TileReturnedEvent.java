package it.polimi.galaxy_trucker.server.events.shipBuilding;

import it.polimi.galaxy_trucker.server.events.EventModel;

public class TileReturnedEvent extends EventModel {

    private final int tileId;

    public TileReturnedEvent(int tileId) {
        this.tileId = tileId;
    }

    public int getTileId() {
        return tileId;
    }
}
