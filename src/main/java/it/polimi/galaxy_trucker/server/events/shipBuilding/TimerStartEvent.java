package it.polimi.galaxy_trucker.server.events.shipBuilding;

import it.polimi.galaxy_trucker.server.events.EventModel;

public class TimerStartEvent extends EventModel {

    private final int duration;
    private final int timesFlipped;

    public TimerStartEvent(int duration, int timesFlipped) {
        this.duration = duration;
        this.timesFlipped = timesFlipped;
    }

    public int getDuration() {
        return duration;
    }

    public int getTimesFlipped() {
        return timesFlipped;
    }
}
