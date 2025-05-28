package it.polimi.galaxy_trucker.server.controller.commandHandlers;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;

import java.util.Map;

public class WaitingCmdHandler extends CommandHandler {

    public WaitingCmdHandler(GameModel gameModel, Map<VirtualView, Player> playerMap, MessageDispatcher dispatcher) {
        super(gameModel, playerMap, dispatcher);
    }

    @Override
    public void handle(VirtualView senderView, JsonNode cmd) {
        Logger.logWarning("Server is currently waiting, ignoring command: " + cmd);
    }
}
