package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.common.messages.flight.ShipBoardMessage;
import it.polimi.galaxy_trucker.common.messages.flight.TileInfo;
import it.polimi.galaxy_trucker.server.events.shipBuilding.*;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.*;
import it.polimi.galaxy_trucker.common.messages.login.LobbyJoinResponse;
import it.polimi.galaxy_trucker.common.messages.shipBuilding.*;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.events.*;
import it.polimi.galaxy_trucker.server.model.GameModel.*;

import java.util.HashMap;
import java.util.List;
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
                GameState.WAITING, new WaitingHandler(gameModel, playerMap),
                GameState.GAME_OVER, new GameOverHandler(gameModel, playerMap)
        );

        addEventListeners();
    }

    private void addEventListeners() {

        gameModel.addEventListener(PlayersUpdateEvent.class, this::handlePlayersUpdate);
        gameModel.addEventListener(BoardTypeUpdateEvent.class, this::handleBoardTypeUpdate);
        gameModel.addEventListener(GameStateUpdateEvent.class, this::handleGameStateUpdate);
        gameModel.addEventListener(TileTakenEvent.class, this::handleTileTaken);
        gameModel.addEventListener(TileReturnedEvent.class, this::handleTileReturned);
        gameModel.addEventListener(CardsTakenEvent.class, this::handleCardsTaken);
        gameModel.addEventListener(CardsReturnedEvent.class, this::handleCardReturned);
        gameModel.addEventListener(GameResetEvent.class, this::handleGameReset);
        gameModel.addEventListener(TimerStartEvent.class, this::handleTimerStart);
        gameModel.addEventListener(TimerFinishedEvent.class, this::handleTimerFinished);
        gameModel.addEventListener(DoneBuildingEvent.class, this::handleDoneBuilding);
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
        if(event.getGameState() == GameState.WAITING) {
            if(event.getPreviousGameState() == GameState.LOBBY) {
                broadcastMessage(new BoardTypeMessage(gameModel.getBoardType()));
            }
        }
        else if(event.getGameState() == GameState.FLIGHT_PHASE) {
            System.out.println("A "+ playerMap.size());
            for(Player player : gameModel.getSortedPlayers()) {
                System.out.println("B " + new ShipBoardMessage(player.getName(), FlightHandler.getSimplifiedShipBoard(player.getShipBoard())));
                //broadcastMessage(new ShipBoardMessage(player.getName(), FlightHandler.getSimplifiedShipBoard(player.getShipBoard())));

                System.out.println("Sending board for " + player.getName());
                List<TileInfo> tiles = FlightHandler.getSimplifiedShipBoard(player.getShipBoard());
                System.out.println("tiles " + tiles.size());
                ShipBoardMessage msg = new ShipBoardMessage(player.getName(), tiles);
                System.out.println("Content: " + msg);
                broadcastMessage(msg);
            }
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
    private void handleTimerStart(TimerStartEvent event) {
        Logger.logDebug("Started timer " + event.getTimesFlipped() + " of " + event.getDuration() + " seconds");
        broadcastMessage(new TimerStartMessage(event.getDuration(), event.getTimesFlipped()));
    }
    private void handleTimerFinished(TimerFinishedEvent event) {
        Logger.logDebug(event.isFinalFlip()? "Final timer ended" : "Timer ended");
        broadcastMessage(new TimerFinishedMessage(event.isFinalFlip()));
    }
    private void handleDoneBuilding(DoneBuildingEvent event) {
        Logger.logDebug(event.getPlayerName() + " finished building and took position " + event.getPosition());
        broadcastMessage(new DoneBuildingMessage(event.getPlayerName(), event.getPosition()));
    }


    private void broadcastMessage(Object message) {
        synchronized (playerMap) {
            for (VirtualView virtualView : playerMap.keySet()) {
                virtualView.sendMessage(message);
            }
        }
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
