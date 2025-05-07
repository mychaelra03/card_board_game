package it.polimi.galaxy_trucker.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.*;
import it.polimi.galaxy_trucker.common.messages.lobby.BoardSelectMessage;
import it.polimi.galaxy_trucker.common.messages.lobby.ChatMessage;
import it.polimi.galaxy_trucker.common.messages.lobby.KickedMessage;
import it.polimi.galaxy_trucker.common.messages.login.LobbyJoinResponse;
import it.polimi.galaxy_trucker.common.messages.shipBuilding.*;
import it.polimi.galaxy_trucker.server.events.*;
import it.polimi.galaxy_trucker.server.events.shipBuilding.CardsReturnedEvent;
import it.polimi.galaxy_trucker.server.events.shipBuilding.CardsTakenEvent;
import it.polimi.galaxy_trucker.server.events.shipBuilding.TileReturnedEvent;
import it.polimi.galaxy_trucker.server.events.shipBuilding.TileTakenEvent;
import it.polimi.galaxy_trucker.server.model.cards.Card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    GameModel gameModel;
    private final Map<VirtualView, Player> playerMap = new HashMap<>();

    private final Map<VirtualView, Integer> tileMap = new HashMap<>();
    private final Map<VirtualView, Integer> cardsMap = new HashMap<>();

    public Controller(GameModel gameModel) {
        this.gameModel = gameModel;

        gameModel.addEventListener(PlayersUpdateEvent.class, this::handlePlayersUpdate);
        gameModel.addEventListener(BoardTypeUpdateEvent.class, this::handleBoardTypeUpdate);
        gameModel.addEventListener(GameStateUpdateEvent.class, this::handleGameStateUpdate);
        gameModel.addEventListener(TileTakenEvent.class, this::handleTileTaken);
        gameModel.addEventListener(TileReturnedEvent.class, this::handleTileReturned);
        gameModel.addEventListener(CardsTakenEvent.class, this::handleCardsTaken);
        gameModel.addEventListener(CardsReturnedEvent.class, this::handleCardReturned);
    }

    private void handlePlayersUpdate(PlayersUpdateEvent event) {
        broadcastMessage(new PlayersInfoMessage(event.getPlayerInfoList(), event.getUsername()));
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


    public void processCmd(VirtualView senderVirtualView, String json) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode cmd = mapper.readTree(json);
        String type = cmd.get("type").asText();

        switch (type) {
            case "playerInfo" -> {

                PlayerInfo playerInfo = mapper.convertValue(cmd, PlayerInfo.class);

                Player player = new Player(playerInfo.getUsername(), playerInfo.getProfilePicIds());

                LobbyJoinResponse.JoinStatus result = gameModel.canAddPlayer(player);

                senderVirtualView.sendMessage(new LobbyJoinResponse(result));

                if (result == LobbyJoinResponse.JoinStatus.SUCCESS) {
                    playerMap.put(senderVirtualView, player);
                    gameModel.addPlayer(player);
                    senderVirtualView.sendMessage(new BoardTypeMessage(gameModel.getBoardType()));
                } else senderVirtualView.close();
            }

            case "boardSelect" -> {
                if(!playerMap.get(senderVirtualView).isAdmin()) {
                    Logger.logWarning("You do not have the permission to change board type.");
                    return;
                }
                BoardSelectMessage boardSelectMessage = mapper.convertValue(cmd, BoardSelectMessage.class);
                gameModel.cycleBoard(boardSelectMessage.getAction());
            }

            case "readyToggle" -> {
                Player player = playerMap.get(senderVirtualView);
                if (player == null) throw new RuntimeException("Player not found");
                player.toggleReady();
                gameModel.updatePlayersReady();
            }

            case "chatMessage" -> {

                ChatMessage chatMessage = mapper.convertValue(cmd, ChatMessage.class);

                Player sender = playerMap.get(senderVirtualView);

                ChatMessage completeChatMessage = new ChatMessage(sender.getName(), sender.getColor().toString(), chatMessage.getMessage());

                playerMap.forEach((v, _) -> v.sendMessage(completeChatMessage));
            }

            case "kickPlayer" -> {
                if(!playerMap.get(senderVirtualView).isAdmin()) {
                    Logger.logWarning("You need to be an admin to kick another player.");
                    return;
                }
                PlayerInfo kickedPlayerInfo = mapper.convertValue(cmd, PlayerInfo.class);

                VirtualView viewToKick = getVirtualViewByPlayerName(kickedPlayerInfo.getUsername());

                if (viewToKick != null) {
                    viewToKick.sendMessage(new KickedMessage());
                    viewToKick.close();
                }
            }

            case "promotePlayer" -> {
                if(!playerMap.get(senderVirtualView).isAdmin()) {
                    Logger.logWarning("You need to be an admin to promote another player.");
                    return;
                }
                PlayerInfo promotedPlayerInfo = mapper.convertValue(cmd, PlayerInfo.class);

                gameModel.setNewAdmin(promotedPlayerInfo.getUsername());
            }

            case "tileTaken" -> {
                if(isPlayerHoldingItem(senderVirtualView)) return;

                TileTakenMessage tileTakenMessage = mapper.convertValue(cmd, TileTakenMessage.class);

                int id = tileTakenMessage.getTileId();
                String playerName = playerMap.get(senderVirtualView).getName();

                if(gameModel.TryTakeTile(playerName, id)) {
                    tileMap.put(senderVirtualView, id);
                }
            }

            case "tileReturned" -> {
                if(!tileMap.containsKey(senderVirtualView)) {
                    Logger.logWarning("Player was not holding a tile");
                    return;
                }
                int tileId = tileMap.get(senderVirtualView);
                tileMap.remove(senderVirtualView);

                gameModel.returnTile(tileId);
            }

            case "tileFixed" -> {
                if(!tileMap.containsKey(senderVirtualView)) {
                    Logger.logWarning("Player was not holding a tile");
                    return;
                }

                TileFixedMessage message = mapper.convertValue(cmd, TileFixedMessage.class);

                int tileId = tileMap.get(senderVirtualView);
                int x = message.getX();
                int y = message.getY();
                int rotation = message.getRotation();

                tileMap.remove(senderVirtualView);
                playerMap.get(senderVirtualView).setTile(x, y, tileId, rotation);
            }

            case "cardsTaken" -> {
                if(isPlayerHoldingItem(senderVirtualView)) return;

                CardsTakenMessage cardsTakenMessage = mapper.convertValue(cmd, CardsTakenMessage.class);

                int id = cardsTakenMessage.getPileId();
                String playerName = playerMap.get(senderVirtualView).getName();

                if(gameModel.tryTakeCardPile(playerName, id)) {
                    cardsMap.put(senderVirtualView, id);

                    List<Card> pile = gameModel.getPile(id);
                    int[] levelOneCards = pile.stream()
                            .filter(card -> card.getLevel() == 1)
                            .mapToInt(Card::getId).toArray();
                    int[] levelTwoCards = pile.stream()
                            .filter(card -> card.getLevel() == 2)
                            .mapToInt(Card::getId).toArray();

                    senderVirtualView.sendMessage(new CardsMessage(levelOneCards, levelTwoCards));
                }
            }

            case "cardsReturned" -> {
                if(!cardsMap.containsKey(senderVirtualView)) {
                    Logger.logWarning("Player was not holding cards");
                    return;
                }
                int pileId = cardsMap.get(senderVirtualView);
                cardsMap.remove(senderVirtualView);

                gameModel.returnCardPile(pileId);
            }

            default -> Logger.logError("Unrecognized command: " + type);
        }
    }

    private boolean isPlayerHoldingItem(VirtualView senderVirtualView) {
        if(!tileMap.containsKey(senderVirtualView) && !cardsMap.containsKey(senderVirtualView)) return false;

        Logger.logWarning("Player " + playerMap.get(senderVirtualView).getName() + " is already holding an item.");
        return true;
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

    public void removeClient(VirtualView virtualView) {
        if(playerMap.containsKey(virtualView)) {
            String playerName = playerMap.get(virtualView).getName();
            playerMap.remove(virtualView);
            this.gameModel.removePlayer(playerName);
        }
    }

}
