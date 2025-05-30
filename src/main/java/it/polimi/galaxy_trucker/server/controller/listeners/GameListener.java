package it.polimi.galaxy_trucker.server.controller.listeners;

import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.BoardTypeMessage;
import it.polimi.galaxy_trucker.common.messages.GameResetMessage;
import it.polimi.galaxy_trucker.common.messages.GameStateMessage;
import it.polimi.galaxy_trucker.common.messages.PlayersInfoMessage;
import it.polimi.galaxy_trucker.common.messages.endGame.PlayersScoresMessage;
import it.polimi.galaxy_trucker.common.messages.flight.DeckLevelsMessage;
import it.polimi.galaxy_trucker.common.messages.flight.PickAvailableMessage;
import it.polimi.galaxy_trucker.common.messages.flight.PlayerPositionMessage;
import it.polimi.galaxy_trucker.common.messages.flight.ShipBoardMessage;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.events.game.GameResetEvent;
import it.polimi.galaxy_trucker.server.events.game.GameStateUpdateEvent;
import it.polimi.galaxy_trucker.server.events.gameOver.GameOverEvent;
import it.polimi.galaxy_trucker.server.events.lobby.BoardTypeUpdateEvent;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.Card;

import java.util.List;

public class GameListener extends EventHandler {

    public GameListener(GameModel gameModel, MessageDispatcher dispatcher) {
        super(gameModel, dispatcher);
    }


    @Handles(GameResetEvent.class)
    private void handleGameReset(GameResetEvent event) {
        Logger.logDebug("Game resetting...");
        dispatcher.broadcast(new GameResetMessage(event.getPlayerName()));
        dispatcher.broadcast(new BoardTypeMessage(gameModel.getBoardType()));
    }

    @Handles(BoardTypeUpdateEvent.class)
    private void handleBoardTypeUpdate(BoardTypeUpdateEvent event) {
        Logger.logDebug("Updated board type: " + event.getBoardType());
        dispatcher.broadcast(new BoardTypeMessage(event.getBoardType()));
    }

    @Handles(GameStateUpdateEvent.class)
    private void handleGameStateUpdate(GameStateUpdateEvent event) {
        Logger.logInfo("New Game State: " + event.getGameState().toString().toLowerCase().replace("_", " "));
        dispatcher.broadcast(new GameStateMessage(event.getGameState()));
        sendNewGameStateData(event.getPreviousGameState(), event.getGameState());
    }

    @Handles(GameOverEvent.class)
    private void handleGameOver(GameOverEvent event) {
        dispatcher.broadcast(new PlayersScoresMessage(event.getPlayersScores()));
        dispatcher.broadcast(new PlayersInfoMessage(gameModel.getPlayersInfo(), null));
        dispatcher.broadcast(new BoardTypeMessage(gameModel.getBoardType()));
    }


    private void sendNewGameStateData(GameModel.GameState previousGameState, GameModel.GameState gameState) {
        switch(gameState) {
            case WAITING -> {
                if(previousGameState == GameModel.GameState.LOBBY) {
                    dispatcher.broadcast(new BoardTypeMessage(gameModel.getBoardType()));
                    dispatcher.broadcast(new PlayersInfoMessage(gameModel.getPlayersInfo(), null));
                }}
            case CORRECTING_SHIP -> {
                dispatcher.broadcast(new BoardTypeMessage(gameModel.getBoardType()));
                for(Player player : gameModel.getSortedPlayers()) {
                    String playerName = player.getName();
                    dispatcher.broadcast(new ShipBoardMessage(playerName, player.getSimplifiedShipBoard()));
                    dispatcher.broadcast(new PlayerPositionMessage(playerName, player.getPosition()));
                }
                gameModel.shuffleDeck();
                List<Integer> cardsLevel = gameModel.getDeck().stream().map(Card::getLevel).toList();
                dispatcher.broadcast(new DeckLevelsMessage(cardsLevel));
            }
            case PICKING_CARDS -> dispatcher.broadcast(new PickAvailableMessage(gameModel.getFirstPlayer().getName()));
        }
    }
}
