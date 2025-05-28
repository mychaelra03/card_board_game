package it.polimi.galaxy_trucker.server.controller.eventHandlers;

import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.model.GameModel;

public abstract class EventHandler {
    protected final GameModel gameModel;
    protected final MessageDispatcher dispatcher;

    public EventHandler(GameModel gameModel, MessageDispatcher dispatcher) {
        this.gameModel = gameModel;
        this.dispatcher = dispatcher;
    }
}