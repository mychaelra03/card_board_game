package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.common.messages.flight.SelectedTileMessage;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.VirtualView;

import java.util.Map;

public class CorrectingShipHandler extends CommandHandler {

    public CorrectingShipHandler(GameModel gameModel, Map<VirtualView, Player> playerMap) {
        super(gameModel, playerMap);
    }

    @Override
    public void handle(VirtualView senderView, JsonNode cmd) {

        ObjectMapper mapper = new ObjectMapper();
        String type = cmd.get("type").asText();

        if (type.equals("selected_tile")) {
            handleSelectedTile(senderView, mapper.convertValue(cmd, SelectedTileMessage.class));
        } else {
            Logger.logError("Unrecognized command: " + type);
        }
    }

    private void handleSelectedTile(VirtualView senderView, SelectedTileMessage message) {
        Player player = playerMap.get(senderView);

        gameModel.removeSelectedTile(player, message.getX(), message.getY());
    }
}
