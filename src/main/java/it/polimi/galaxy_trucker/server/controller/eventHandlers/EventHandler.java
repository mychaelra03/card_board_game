package it.polimi.galaxy_trucker.server.controller.eventHandlers;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;

import java.util.Map;

public abstract class EventHandler {
    protected final GameModel gameModel;
    protected final MessageDispatcher dispatcher;

    public EventHandler(GameModel gameModel, MessageDispatcher dispatcher) {
        this.gameModel = gameModel;
        this.dispatcher = dispatcher;
    }
}