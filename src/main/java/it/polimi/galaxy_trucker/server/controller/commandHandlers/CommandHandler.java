package it.polimi.galaxy_trucker.server.controller.commandHandlers;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.VirtualView;

import java.util.Map;

public abstract class CommandHandler {
    protected final GameModel gameModel;
    protected final Map<VirtualView, Player> playerMap;
    protected final MessageDispatcher dispatcher;

    public CommandHandler(GameModel gameModel, Map<VirtualView, Player> playerMap, MessageDispatcher dispatcher) {
        this.gameModel = gameModel;
        this.playerMap = playerMap;
        this.dispatcher = dispatcher;
    }

    public abstract void handle(VirtualView senderView, JsonNode cmd);
}