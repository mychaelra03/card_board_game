package it.polimi.galaxy_trucker.Server.Event;

public interface ModelEventListener <T extends EventModel> {
    void onEvent(T event);
}
