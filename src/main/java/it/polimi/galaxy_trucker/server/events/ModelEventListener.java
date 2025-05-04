package it.polimi.galaxy_trucker.server.events;

public interface ModelEventListener <T extends EventModel> {
    void onEvent(T event);
}
