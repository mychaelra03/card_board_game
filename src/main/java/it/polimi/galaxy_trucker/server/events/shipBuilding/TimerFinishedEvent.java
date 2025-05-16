package it.polimi.galaxy_trucker.server.events.shipBuilding;

import it.polimi.galaxy_trucker.server.events.EventModel;

public class TimerFinishedEvent extends EventModel {

    private final boolean finalFlip;

    public TimerFinishedEvent(boolean finalFlip) {
        this.finalFlip = finalFlip;
    }

    public boolean isFinalFlip() {
        return finalFlip;
    }
}
