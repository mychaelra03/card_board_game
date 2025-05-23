package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.common.Logger;
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

            case "tile_taken"        -> handleTileTaken(senderView, mapper.convertValue(cmd, TileTakenMessage.class));
            case "tile_random"       -> handleTileRandom(senderView);
            case "tile_returned"     -> handleTileReturned(senderView);
            case "tile_fixed"        -> handleTileFixed(senderView, mapper.convertValue(cmd, TileFixedMessage.class));
            case "reserve_tile"      -> handleReserveTile(senderView, mapper.convertValue(cmd, ReserveTileMessage.class));
            case "use_reserved_tile" -> handleUseReservedTile(senderView, mapper.convertValue(cmd, UseReservedTileMessage.class));
            case "cards_taken"       -> handleCardsTaken(senderView, mapper.convertValue(cmd, CardsTakenMessage.class));
            case "cards_returned"    -> handleCardsReturned(senderView);
            case "timer_flipped"     -> handleTimerFlipped(senderView);
            case "done_building"     -> handleDoneBuilding(senderView, mapper.convertValue(cmd, DoneBuildingMessage.class));

            default                  -> Logger.logError("Unrecognized command: " + type);
        }
    }

    private void handleTileTaken(VirtualView senderView, TileTakenMessage message) {
        if(isPlayerHoldingItem(senderView)) return;

        int id = message.getTileId();
        Player player = playerMap.get(senderView);

        if(gameModel.TryTakeTile(player, id)) {
            tileMap.put(senderView, id);
        }
    }

    private void handleTileRandom(VirtualView senderView) {
        if(isPlayerHoldingItem(senderView)) return;

        Player player = playerMap.get(senderView);

        Integer takenTile = gameModel.TakeRandomTile(player);
        if(takenTile != null) {
            tileMap.put(senderView, takenTile);
        }
    }

    private void handleTileReturned(VirtualView senderView) {
        if(!tileMap.containsKey(senderView)) {
            Logger.logWarning("Player was not holding a tile");
            return;
        }
        int tileId = tileMap.get(senderView);
        tileMap.remove(senderView);

        gameModel.returnTile(tileId);
    }

    private void handleTileFixed(VirtualView senderView, TileFixedMessage message) {
        if(!tileMap.containsKey(senderView)) {
            Logger.logWarning("Player was not holding a tile");
            return;
        }

        int tileId = tileMap.get(senderView);
        int x = message.getX();
        int y = message.getY();
        int rotation = message.getRotation();

        tileMap.remove(senderView);
        playerMap.get(senderView).setTile(x, y, tileId, rotation);
    }

    private void handleReserveTile(VirtualView senderView, ReserveTileMessage message) {
        if(!tileMap.containsKey(senderView)) {
            Logger.logWarning("Player was not holding a tile");
            return;
        }

        int tileId = tileMap.get(senderView);
        int slotId = message.getSlotId();

        tileMap.remove(senderView);
        playerMap.get(senderView).reserveTile(tileId, slotId);
    }

    private void handleUseReservedTile(VirtualView senderView, UseReservedTileMessage message) {
        if(isPlayerHoldingItem(senderView)) return;


        int slotId = message.getSlotId();
        int tileId = playerMap.get(senderView).useReservedTile(slotId);

        tileMap.put(senderView, tileId);
    }

    private void handleCardsTaken(VirtualView senderView, CardsTakenMessage cardsTakenMessage) {
        if(isPlayerHoldingItem(senderView)) return;

        int id = cardsTakenMessage.getPileId();
        Player player = playerMap.get(senderView);

        if(gameModel.tryTakeCardPile(player, id)) {
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

    private void handleCardsReturned(VirtualView senderView) {
        if(!cardsMap.containsKey(senderView)) {
            Logger.logWarning("Player was not holding cards");
            return;
        }
        int pileId = cardsMap.get(senderView);
        cardsMap.remove(senderView);

        gameModel.returnCardPile(pileId);
    }

    private void handleTimerFlipped(VirtualView senderView) {
        boolean hasPlayerFinishedBuilding = playerMap.get(senderView).getState() == Player.PlayerState.DONE_BUILDING;

        gameModel.flipTimer(hasPlayerFinishedBuilding);
    }

    private void handleDoneBuilding(VirtualView senderView, DoneBuildingMessage message) {
        int positionIndex = message.getPositionIndex();
        Player player = playerMap.get(senderView);

        gameModel.claimPosition(player, positionIndex);
    }


    private boolean isPlayerHoldingItem(VirtualView senderVirtualView) {
        if(!tileMap.containsKey(senderVirtualView) && !cardsMap.containsKey(senderVirtualView)) return false;

        Logger.logWarning("Player " + playerMap.get(senderVirtualView).getName() + " is already holding an item.");
        return true;
    }
}
