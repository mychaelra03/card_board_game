package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.VirtualView;

import java.util.Map;

public abstract class CommandHandler {
    protected final GameModel gameModel;
    protected final Map<VirtualView, Player> playerMap;

    public CommandHandler(GameModel gameModel, Map<VirtualView, Player> playerMap) {
        this.gameModel = gameModel;
        this.playerMap = playerMap;
    }

    public abstract void handle(VirtualView senderView, JsonNode cmd);
}