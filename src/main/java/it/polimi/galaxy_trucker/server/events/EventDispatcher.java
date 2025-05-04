package it.polimi.galaxy_trucker.server.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDispatcher {
    private final Map<Class<? extends EventModel>, List<ModelEventListener<? extends EventModel>>> listeners = new HashMap<>();

    public <T extends EventModel> void  addListener(Class<T> eventType, ModelEventListener<T> listener) {
        listeners.computeIfAbsent(eventType, _ -> new ArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends EventModel> void fireEvent(T event) {
        List<ModelEventListener<? extends EventModel>> registered = listeners.get(event.getClass());
        if (registered != null) {
            for (ModelEventListener<? extends EventModel> listener : registered) {
                ((ModelEventListener<T>) listener).onEvent(event);
            }
        }
    }



}
