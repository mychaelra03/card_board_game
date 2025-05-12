package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.PlayerInfo;
import it.polimi.galaxy_trucker.common.messages.lobby.BoardSelectMessage;
import it.polimi.galaxy_trucker.common.messages.lobby.ChatMessage;
import it.polimi.galaxy_trucker.common.messages.lobby.KickedMessage;
import it.polimi.galaxy_trucker.server.VirtualView;

import java.util.Map;

public class GameOverHandler extends CommandHandler {

    public GameOverHandler(GameModel gameModel, Map<VirtualView, Player> playerMap) {
        super(gameModel, playerMap);
    }

    @Override
    public void handle(VirtualView senderView, JsonNode cmd) {

        ObjectMapper mapper = new ObjectMapper();
        String type = cmd.get("type").asText();
        switch (type) {

            default -> Logger.logError("Unrecognized command: " + type);
        }
    }
}
