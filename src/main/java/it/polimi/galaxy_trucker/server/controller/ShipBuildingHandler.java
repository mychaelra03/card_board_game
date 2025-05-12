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
import it.polimi.galaxy_trucker.common.messages.shipBuilding.*;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.model.cards.Card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShipBuildingHandler extends CommandHandler {

    public ShipBuildingHandler(GameModel gameModel, Map<VirtualView, Player> playerMap) {
        super(gameModel, playerMap);
    }

    private final Map<VirtualView, Integer> tileMap = new HashMap<>();
    private final Map<VirtualView, Integer> cardsMap = new HashMap<>();

    @Override
    public void handle(VirtualView senderView, JsonNode cmd) {

        ObjectMapper mapper = new ObjectMapper();
        String type = cmd.get("type").asText();
        switch (type) {


            case "tileTaken" -> {
                if(isPlayerHoldingItem(senderView)) return;

                TileTakenMessage message = mapper.convertValue(cmd, TileTakenMessage.class);

                int id = message.getTileId();
                String playerName = playerMap.get(senderView).getName();

                if(gameModel.TryTakeTile(playerName, id)) {
                    tileMap.put(senderView, id);
                }
            }

            case "tileReturned" -> {
                if(!tileMap.containsKey(senderView)) {
                    Logger.logWarning("Player was not holding a tile");
                    return;
                }
                int tileId = tileMap.get(senderView);
                tileMap.remove(senderView);

                gameModel.returnTile(tileId);
            }

            case "tileFixed" -> {
                if(!tileMap.containsKey(senderView)) {
                    Logger.logWarning("Player was not holding a tile");
                    return;
                }

                TileFixedMessage message = mapper.convertValue(cmd, TileFixedMessage.class);

                int tileId = tileMap.get(senderView);
                int x = message.getX();
                int y = message.getY();
                int rotation = message.getRotation();

                tileMap.remove(senderView);
                playerMap.get(senderView).setTile(x, y, tileId, rotation);
            }

            case "reserveTile" -> {
                if(!tileMap.containsKey(senderView)) {
                    Logger.logWarning("Player was not holding a tile");
                    return;
                }

                ReserveTileMessage message = mapper.convertValue(cmd, ReserveTileMessage.class);

                int tileId = tileMap.get(senderView);
                int slotId = message.getSlotId();

                tileMap.remove(senderView);
                playerMap.get(senderView).reserveTile(tileId, slotId);
            }

            case "useReservedTile" -> {
                if(isPlayerHoldingItem(senderView)) return;

                UseReservedTileMessage message = mapper.convertValue(cmd, UseReservedTileMessage.class);

                int slotId = message.getSlotId();
                int tileId = playerMap.get(senderView).useReservedTile(slotId);

                tileMap.put(senderView, tileId);
            }

            case "cardsTaken" -> {
                if(isPlayerHoldingItem(senderView)) return;

                CardsTakenMessage cardsTakenMessage = mapper.convertValue(cmd, CardsTakenMessage.class);

                int id = cardsTakenMessage.getPileId();
                String playerName = playerMap.get(senderView).getName();

                if(gameModel.tryTakeCardPile(playerName, id)) {
                    cardsMap.put(senderView, id);

                    List<Card> pile = gameModel.getPile(id);
                    int[] levelOneCards = pile.stream()
                            .filter(card -> card.getLevel() == 1)
                            .mapToInt(Card::getId).toArray();
                    int[] levelTwoCards = pile.stream()
                            .filter(card -> card.getLevel() == 2)
                            .mapToInt(Card::getId).toArray();

                    senderView.sendMessage(new CardsMessage(levelOneCards, levelTwoCards));
                }
            }

            case "cardsReturned" -> {
                if(!cardsMap.containsKey(senderView)) {
                    Logger.logWarning("Player was not holding cards");
                    return;
                }
                int pileId = cardsMap.get(senderView);
                cardsMap.remove(senderView);

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
}
