package it.polimi.galaxy_trucker.server.model;

import it.polimi.galaxy_trucker.server.events.EventDispatcher;
import it.polimi.galaxy_trucker.server.events.shipBuilding.TimerStartEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BuildingTimerHandler {
    private ScheduledExecutorService executor;
    private final int delaySeconds;
    private final EventDispatcher dispatcher;
    private final Runnable onFinished;

    private int timesFlipped = 0;

    public BuildingTimerHandler(int delaySeconds, EventDispatcher dispatcher, Runnable onFinished) {
        this.delaySeconds = delaySeconds;
        this.dispatcher = dispatcher;
        this.onFinished = onFinished;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        timesFlipped++;
        if (dispatcher != null)
            dispatcher.fireEvent(new TimerStartEvent(delaySeconds, timesFlipped));

        executor.schedule(() -> {
            if (onFinished != null) onFinished.run();
        }, delaySeconds, TimeUnit.SECONDS);
    }

    public int getTimesFlipped() {
        return timesFlipped;
    }

    public void reset() {
        timesFlipped = 0;
        executor.shutdownNow();
        executor = Executors.newSingleThreadScheduledExecutor();
    }
}