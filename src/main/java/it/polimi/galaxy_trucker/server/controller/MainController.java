package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.*;
import it.polimi.galaxy_trucker.common.messages.lobby.KickedMessage;
import it.polimi.galaxy_trucker.common.messages.login.LobbyJoinResponse;
import it.polimi.galaxy_trucker.common.messages.shipBuilding.*;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.events.*;
import it.polimi.galaxy_trucker.server.events.shipBuilding.CardsReturnedEvent;
import it.polimi.galaxy_trucker.server.events.shipBuilding.CardsTakenEvent;
import it.polimi.galaxy_trucker.server.events.shipBuilding.TileReturnedEvent;
import it.polimi.galaxy_trucker.server.events.shipBuilding.TileTakenEvent;
import it.polimi.galaxy_trucker.GameModel.*;

import java.util.HashMap;
import java.util.Map;

public class MainController {

    private final GameModel gameModel;
    private final Map<VirtualView, Player> playerMap = new HashMap<>();

    private final Map<GameState, CommandHandler> handlerMap;

    public MainController(GameModel gameModel) {
        this.gameModel = gameModel;

        this.handlerMap = Map.of(
                GameState.LOBBY, new LobbyHandler(gameModel, playerMap),
                GameState.BUILDING_SHIPS, new ShipBuildingHandler(gameModel, playerMap),
                GameState.FLIGHT_PHASE, new FlightHandler(gameModel, playerMap),
                GameState.GAME_OVER, new GameOverHandler(gameModel, playerMap)
        );

        gameModel.addEventListener(PlayersUpdateEvent.class, this::handlePlayersUpdate);
        gameModel.addEventListener(BoardTypeUpdateEvent.class, this::handleBoardTypeUpdate);
        gameModel.addEventListener(GameStateUpdateEvent.class, this::handleGameStateUpdate);
        gameModel.addEventListener(TileTakenEvent.class, this::handleTileTaken);
        gameModel.addEventListener(TileReturnedEvent.class, this::handleTileReturned);
        gameModel.addEventListener(CardsTakenEvent.class, this::handleCardsTaken);
        gameModel.addEventListener(CardsReturnedEvent.class, this::handleCardReturned);
        gameModel.addEventListener(GameResetEvent.class, this::handleGameReset);
    }

    public void processCommand(VirtualView senderVirtualView, String json) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode cmd = mapper.readTree(json);
        String type = cmd.get("type").asText();

        if(type.equals("playerInfo")) {

            PlayerInfo playerInfo = mapper.convertValue(cmd, PlayerInfo.class);

            Player player = new Player(playerInfo.getUsername(), playerInfo.getProfilePicIds());

            LobbyJoinResponse.JoinStatus result = gameModel.canAddPlayer(player);

            senderVirtualView.sendMessage(new LobbyJoinResponse(result));

            if (result == LobbyJoinResponse.JoinStatus.SUCCESS) {
                senderVirtualView.sendMessage(new BoardTypeMessage(gameModel.getBoardType()));
                playerMap.put(senderVirtualView, player);
                gameModel.addPlayer(player);
            } else senderVirtualView.close();
        }
        else handlerMap.get(gameModel.getGameState()).handle(senderVirtualView, cmd);
    }


    private void handlePlayersUpdate(PlayersUpdateEvent event) {
        broadcastMessage(new PlayersInfoMessage(event.getPlayerInfoList(), event.getUsername()));
    }
    private void handleGameReset(GameResetEvent event) {
        Logger.logDebug("Game resetting...");
        broadcastMessage(new GameResetMessage(event.getPlayerName()));
        broadcastMessage(new BoardTypeMessage(gameModel.getBoardType()));
    }
    private void handleBoardTypeUpdate(BoardTypeUpdateEvent event) {
        Logger.logInfo("Updated board type: " + event.getBoardType());
        broadcastMessage(new BoardTypeMessage(event.getBoardType()));
    }
    private void handleGameStateUpdate(GameStateUpdateEvent event) {
        Logger.logInfo("Updated game state: " + event.getGameState());
        broadcastMessage(new GameStateMessage(event.getGameState()));
        if(event.getGameState() == GameModel.GameState.BUILDING_SHIPS) {
            broadcastMessage(new BoardTypeMessage(gameModel.getBoardType()));
        }
    }
    private void handleTileTaken(TileTakenEvent event) {
        Logger.logDebug(event.getPlayerName() + " took tile: " + event.getTileId());
        broadcastMessage(new TileTakenMessage(event.getPlayerName(), event.getTileId()));
    }
    private void handleTileReturned(TileReturnedEvent event) {
        Logger.logDebug("Tile returned: " + event.getTileId());
        broadcastMessage(new TileReturnedMessage(event.getTileId()));
    }
    private void handleCardsTaken(CardsTakenEvent event) {
        Logger.logDebug(event.getPlayerName() + " took cards: " + event.getPileId());
        broadcastMessage(new CardsTakenMessage(event.getPlayerName(), event.getPileId()));
    }
    private void handleCardReturned(CardsReturnedEvent event) {
        Logger.logDebug("Cards returned: " + event.getPileId());
        broadcastMessage(new CardsReturnedMessage(event.getPileId()));
    }


    private void broadcastMessage(Object message) {
        for (VirtualView virtualView : playerMap.keySet()) {
            virtualView.sendMessage(message);
        }
    }



    public void removeClient(VirtualView virtualView) {
        if(playerMap.containsKey(virtualView)) {
            String playerName = playerMap.get(virtualView).getName();
            playerMap.remove(virtualView);
            this.gameModel.removePlayer(playerName);
        }
    }
}
