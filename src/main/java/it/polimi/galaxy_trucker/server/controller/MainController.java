package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.common.messages.flight.*;
import it.polimi.galaxy_trucker.common.messages.flight.cardActions.*;
import it.polimi.galaxy_trucker.server.events.flight.*;
import it.polimi.galaxy_trucker.server.events.flight.cards.*;
import it.polimi.galaxy_trucker.server.events.lobby.BoardTypeUpdateEvent;
import it.polimi.galaxy_trucker.server.events.lobby.PlayersUpdateEvent;
import it.polimi.galaxy_trucker.server.events.shipBuilding.*;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.*;
import it.polimi.galaxy_trucker.common.messages.login.JoinResponseMessage;
import it.polimi.galaxy_trucker.common.messages.shipBuilding.*;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.events.*;
import it.polimi.galaxy_trucker.server.model.GameModel.*;
import it.polimi.galaxy_trucker.server.model.cards.Card;

import java.util.Arrays;
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
                GameState.CORRECTING_SHIP, new CorrectingShipHandler(gameModel, playerMap),
                GameState.ASSIGNING_CREW, new AssigningCrewHandler(gameModel, playerMap),
                GameState.PICKING_CARDS, new PickingCardsHandler(gameModel, playerMap),
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
        gameModel.addEventListener(PlayerPositionUpdateEvent.class, this::handlePlayerPositionUpdate);
        gameModel.addEventListener(ShipUpdateEvent.class, this::handleShipUpdate);
        gameModel.addEventListener(InvalidShipEvent.class, this::handleInvalidShip);
        gameModel.addEventListener(CrewUpdateEvent.class, this::handleCrewUpdate);
        gameModel.addEventListener(CreditsUpdateEvent.class, this::handleCreditsUpdate);
        gameModel.addEventListener(AlienCabinTilesEvent.class, this::handleAlienCabinTiles);
        gameModel.addEventListener(CardPickedEvent.class, this::handleCardPicked);
        gameModel.addEventListener(CardFinishedEvent.class, this::handleCardFinished);
        gameModel.addEventListener(PlanetSelectionEvent.class, this::handlePlanetSelection);
        gameModel.addEventListener(PlanetChosenEvent.class, this::handlePlanetChosen);
        gameModel.addEventListener(PlanetRefusedEvent.class, this::handlePlanetRefused);
        gameModel.addEventListener(CargoDecisionEvent.class, this::handleCargoDecision);
        gameModel.addEventListener(CargoSwappedEvent.class, this::handleCargoSwapped);
        gameModel.addEventListener(CargoUnloadedEvent.class, this::handleCargoUnloaded);
        gameModel.addEventListener(CargoDoneEvent.class, this::handleCargoDone);
        gameModel.addEventListener(CardDecisionEvent.class, this::handleCardDecision);

    }

    public void processCommand(VirtualView senderVirtualView, String json) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode cmd = mapper.readTree(json);
        String type = cmd.get("type").asText();

        if(type.equals("player_info")) {
            handlePlayerInfoMessage(senderVirtualView, mapper.convertValue(cmd, PlayerInfo.class));
        }
        else {
            CommandHandler handler = handlerMap.get(gameModel.getGameState());
            if (handler != null) {
                handler.handle(senderVirtualView, cmd);
            } else {
                Logger.logError("No handler for state: " + gameModel.getGameState());
            }
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

    // GameModel updates

    private void handlePlayersUpdate(PlayersUpdateEvent event) {
        broadcast(new PlayersInfoMessage(event.getPlayerInfoList(), event.getUsername()));
    }
    private void handleGameReset(GameResetEvent event) {
        Logger.logDebug("Game resetting...");
        broadcast(new GameResetMessage(event.getPlayerName()));
        broadcast(new BoardTypeMessage(gameModel.getBoardType()));
    }
    private void handleBoardTypeUpdate(BoardTypeUpdateEvent event) {
        Logger.logInfo("Updated board type: " + event.getBoardType());
        broadcast(new BoardTypeMessage(event.getBoardType()));
    }
    private void handleGameStateUpdate(GameStateUpdateEvent event) {
        Logger.logInfo("Updated game state: " + event.getGameState());
        broadcast(new GameStateMessage(event.getGameState()));
        if(event.getGameState() == GameState.WAITING) {
            if(event.getPreviousGameState() == GameState.LOBBY) {
                broadcast(new BoardTypeMessage(gameModel.getBoardType()));
            }
        }
        else if(event.getGameState() == GameState.CORRECTING_SHIP) {
            broadcast(new BoardTypeMessage(gameModel.getBoardType()));
            for(Player player : gameModel.getSortedPlayers()) {
                String playerName = player.getName();
                broadcast(new ShipBoardMessage(playerName, player.getSimplifiedShipBoard()));
                broadcast(new PlayerPositionMessage(playerName, player.getPosition()));
            }
            gameModel.shuffleDeck();
            List<Integer> cardsLevel = gameModel.getDeck().stream().map(Card::getLevel).toList();
            broadcast(new DeckLevelsMessage(cardsLevel));
        }
        else if(event.getGameState() == GameState.PICKING_CARDS) {
            broadcast(new PickAvailableMessage(gameModel.getFirstPlayer().getName()));
        }
    }
    private void handleTileTaken(TileTakenEvent event) {
        Logger.logDebug(event.getPlayerName() + " took tile: " + event.getTileId());
        broadcast(new TileTakenMessage(event.getPlayerName(), event.getTileId()));
    }
    private void handleTileReturned(TileReturnedEvent event) {
        Logger.logDebug("Tile returned: " + event.getTileId());
        broadcast(new TileReturnedMessage(event.getTileId()));
    }
    private void handleCardsTaken(CardsTakenEvent event) {
        Logger.logDebug(event.getPlayerName() + " took cards: " + event.getPileId());
        broadcast(new CardsTakenMessage(event.getPlayerName(), event.getPileId()));
    }
    private void handleCardReturned(CardsReturnedEvent event) {
        Logger.logDebug("Cards returned: " + event.getPileId());
        broadcast(new CardsReturnedMessage(event.getPileId()));
    }
    private void handleTimerStart(TimerStartEvent event) {
        Logger.logDebug("Started timer " + event.getTimesFlipped() + " of " + event.getDuration() + " seconds");
        broadcast(new TimerStartMessage(event.getDuration(), event.getTimesFlipped()));
    }
    private void handleTimerFinished(TimerFinishedEvent event) {
        Logger.logDebug(event.isFinalFlip()? "Final timer ended" : "Timer ended");
        broadcast(new TimerFinishedMessage(event.isFinalFlip()));
    }
    private void handleDoneBuilding(DoneBuildingEvent event) {
        Logger.logDebug(event.getPlayerName() + " finished building and took position " + event.getPosition());
        broadcast(new DoneBuildingMessage(event.getPlayerName(), event.getPosition()));
    }
    private void handlePlayerPositionUpdate(PlayerPositionUpdateEvent event) {
        Logger.logDebug(event.getPlayerName() + " moved to " + event.getNewPosition());
        broadcast(new PlayerPositionMessage(event.getPlayerName(), event.getNewPosition()));
    }
    private void handleShipUpdate(ShipUpdateEvent event) {
        broadcast(new ShipBoardMessage(event.getPlayerName(), event.getTiles()));
    }
    private void handleInvalidShip(InvalidShipEvent event) {
        VirtualView virtualView = getVirtualViewByPlayerName(event.getPlayerName());
        virtualView.sendMessage(new TilesMessage("invalid_tiles", event.getPlayerName(), event.getInvalidTiles()));
    }
    private void handleCrewUpdate(CrewUpdateEvent event) {
        broadcast(new CrewMessage(event.getPlayerName(), event.getCrew()));
    }
    private void handleCreditsUpdate(CreditsUpdateEvent event) {
        broadcast(new CreditsMessage(event.getPlayerName(), event.getCredits()));
    }
    private void handleAlienCabinTiles(AlienCabinTilesEvent event) {
        VirtualView virtualView = getVirtualViewByPlayerName(event.getPlayerName());
        virtualView.sendMessage(new TilesMessage("alien_cabin_tiles", event.getPlayerName(), event.getCabinTiles()));
    }
    private void handleCardPicked(CardPickedEvent event) {
        Card card = event.getCard();
        Logger.logDebug("Card picked: " + card.getCardType() + " of level " + card.getLevel());
        broadcast(new CardPickedMessage(card.getLevel(), card.getId()));
    }
    private void handleCardFinished(CardFinishedEvent event) {
        if(!gameModel.getDeck().isEmpty()) {
            broadcast(new PickAvailableMessage(gameModel.getFirstPlayer().getName()));
        } else {
            //TODO: finished
        }
    }
    private void handlePlanetSelection(PlanetSelectionEvent event) {
        List<String> planetChosenByNames = Arrays.stream(event.getPlanetChosenBy())
                .map(p -> p != null ? p.getName() : null).toList();
        broadcast(new PlanetSelectionMessage(event.getPlayerName(), planetChosenByNames));
    }
    private void handlePlanetChosen(PlanetChosenEvent event) {
        Logger.logDebug(event.getPlayerName() + " chose planet " + event.getChosenPlanet() + ".");
        broadcast(new PlanetChosenMessage(event.getPlayerName(), event.getChosenPlanet()));
    }
    private void handlePlanetRefused(PlanetRefusedEvent event) {
        Logger.logDebug(event.getPlayerName() + " refused planet.");
        broadcast(new PlanetRefusedMessage(event.getPlayerName()));
    }
    private void handleCargoDecision(CargoDecisionEvent event) {
        for(Player player : event.getPlayers()) {
            broadcast(new CargoDecisionMessage(player.getName(), player.getShipContent().cargoData(), player.getCargoPool()));
        }
    }
    private void handleCargoSwapped(CargoSwappedEvent event) {
        String playerName = event.getPlayer().getName();
        sendMessageTo(playerName, new CargoSwapMessage(playerName, event.getBlockIndex(), event.getTileCoords(), event.getContainerIndex()));
        broadcastExcept(playerName, new CargoMessage(playerName, event.getPlayer().getShipContent().cargoData()));
    }
    private void handleCargoUnloaded(CargoUnloadedEvent event) {
        String playerName = event.getPlayer().getName();
        sendMessageTo(playerName, new CargoUnloadMessage(playerName, event.getTileCoords(), event.getContainerIndex()));
        broadcastExcept(playerName, new CargoMessage(playerName, event.getPlayer().getShipContent().cargoData()));
    }
    private void handleCargoDone(CargoDoneEvent event) {
        String playerName = event.getPlayerName();
        sendMessageTo(playerName, new CargoDoneMessage(playerName));
    }
    private void handleCardDecision(CardDecisionEvent event) {
        broadcast(new CardDecisionMessage(event.getPlayerName(), null));
    }


    private void broadcast(Object message) {
        synchronized (playerMap) {
            for (VirtualView virtualView : playerMap.keySet()) {
                virtualView.sendMessage(message);
            }
        }
    }

    private void broadcastExcept(String playerName, Object message) {
        synchronized (playerMap) {
            for (VirtualView virtualView : playerMap.keySet()) {
                if(playerMap.get(virtualView).getName().equals(playerName)) continue;
                virtualView.sendMessage(message);
            }
        }
    }

    private void sendMessageTo(String playerName, Object message) {
        VirtualView virtualView = getVirtualViewByPlayerName(playerName);
        virtualView.sendMessage(message);
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

    public VirtualView getVirtualViewByPlayerName(String username) {
        for (Map.Entry<VirtualView, Player> entry : playerMap.entrySet()) {
            if (entry.getValue().getName().equals(username)) {
                return entry.getKey();
            }
        }
        Logger.logError("No VirtualView found for player: " + username);
        return null;
    }
}
