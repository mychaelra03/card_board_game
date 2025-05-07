package it.polimi.galaxy_trucker.server.events.shipBuilding;

import it.polimi.galaxy_trucker.server.events.EventModel;

public class CardsReturnedEvent extends EventModel {
    private final int pileId;

    public CardsReturnedEvent(int pileId) {
        this.pileId = pileId;
    }

    public int getPileId() {
        return pileId;
    }
}
