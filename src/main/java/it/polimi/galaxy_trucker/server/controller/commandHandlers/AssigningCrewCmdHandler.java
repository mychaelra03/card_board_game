package it.polimi.galaxy_trucker.server.controller.commandHandlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.flight.SelectedTileMessage;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;

import java.util.Map;

public class AssigningCrewCmdHandler extends CommandHandler {

    public AssigningCrewCmdHandler(GameModel gameModel, Map<VirtualView, Player> playerMap, MessageDispatcher dispatcher) {
        super(gameModel, playerMap, dispatcher);
    }

    @Override
    public void handle(VirtualView senderView, JsonNode cmd) {

        ObjectMapper mapper = new ObjectMapper();
        String type = cmd.get("type").asText();
        switch (type) {

            case "selected_tile" -> handleSelectedTile(senderView, mapper.convertValue(cmd, SelectedTileMessage.class));
            case "done"          -> handleDone(senderView);

            default              -> Logger.logError("Unrecognized command: " + type);
        }
    }

    private void handleSelectedTile(VirtualView senderView, SelectedTileMessage message) {
        Player player = playerMap.get(senderView);

        gameModel.cycleCrewTypeAt(player, message.getX(), message.getY());
    }

    private void handleDone(VirtualView senderView) {
        Player player = playerMap.get(senderView);

        gameModel.doneChoosingCrew(player);
    }
}
