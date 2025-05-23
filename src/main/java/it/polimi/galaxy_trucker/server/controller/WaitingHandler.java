package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;

import java.util.Map;

public class WaitingHandler extends CommandHandler {

    public WaitingHandler(GameModel gameModel, Map<VirtualView, Player> playerMap) {
        super(gameModel, playerMap);
    }

    @Override
    public void handle(VirtualView senderView, JsonNode cmd) {
        Logger.logWarning("Server is currently waiting, ignoring command: " + cmd);
    }
}
