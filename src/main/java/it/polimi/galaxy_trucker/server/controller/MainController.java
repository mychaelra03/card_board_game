package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.server.controller.eventHandlers.*;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.common.messages.*;
import it.polimi.galaxy_trucker.common.messages.login.JoinResponseMessage;
import it.polimi.galaxy_trucker.server.VirtualView;

import java.util.*;

public class MainController {

    private final GameModel gameModel;
    private final Map<VirtualView, Player> playerMap = new HashMap<>();

    MessageDispatcher dispatcher = new MessageDispatcher(playerMap);
    CommandRouter router;

    public MainController(GameModel gameModel) {
        this.gameModel = gameModel;
        this.router = new CommandRouter(gameModel, playerMap, dispatcher);

        initializeListeners();
    }

    private void initializeListeners() {
        new LobbyEventsHandler(gameModel, dispatcher);
        new BuildingEventsHandler(gameModel, dispatcher);
        new GameEventHandler(gameModel, dispatcher);
        new ShipEventsHandler(gameModel, dispatcher);
        new CardsEventsHandler(gameModel, dispatcher);
    }

    public void processCommand(VirtualView senderVirtualView, String json) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode cmd = mapper.readTree(json);
        String type = cmd.get("type").asText();

        if(type.equals("player_info")) {
            handlePlayerInfoMessage(senderVirtualView, mapper.convertValue(cmd, PlayerInfo.class));
        }
        else {
            router.route(gameModel.getGameState(), senderVirtualView, cmd);
        }
    }

    private void handlePlayerInfoMessage(VirtualView senderVirtualView, PlayerInfo playerInfo) {
        Player player = new Player(playerInfo.getUsername(), playerInfo.getProfilePicIds());

        JoinResponseMessage.JoinStatus result = gameModel.canAddPlayer(player);

        senderVirtualView.sendMessage(new JoinResponseMessage(result));

        if (result == JoinResponseMessage.JoinStatus.SUCCESS) {
            player.assignEventDispatcher(gameModel.getEventDispatcher());
            senderVirtualView.sendMessage(new BoardTypeMessage(gameModel.getBoardType()));
            playerMap.put(senderVirtualView, player);
            gameModel.addPlayer(player);
        } else senderVirtualView.close();
    }

    public void removeClient(VirtualView virtualView) {
        synchronized (playerMap) {
            if(playerMap.containsKey(virtualView)) {
                String playerName = playerMap.get(virtualView).getName();
                playerMap.remove(virtualView);
                this.gameModel.removePlayer(playerName);
            }
        }
    }
}
