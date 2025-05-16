package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.common.messages.flight.TileInfo;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.model.ShipBoard;
import it.polimi.galaxy_trucker.server.model.tiles.EmptyTile;
import it.polimi.galaxy_trucker.server.model.tiles.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlightHandler extends CommandHandler {

    public FlightHandler(GameModel gameModel, Map<VirtualView, Player> playerMap) {
        super(gameModel, playerMap);
    }

    @Override
    public void handle(VirtualView senderView, JsonNode cmd) {

        ObjectMapper mapper = new ObjectMapper();
        String type = cmd.get("type").asText();
        switch (type) {

            default -> Logger.logError("Unrecognized command: " + type);
        }
    }

    public static List<TileInfo> getSimplifiedShipBoard(ShipBoard shipBoard) {
        List<TileInfo> simplifiedShipBoard = new ArrayList<>();
        Tile[][] tiles = shipBoard.getTiles();
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 7; x++) {
                Tile tile = tiles[y][x];
                if (tile != null && !(tile instanceof EmptyTile)) {
                    simplifiedShipBoard.add(new TileInfo(tile.getId(), tile.getRotation(), x, y));
                }
            }
        }
        return simplifiedShipBoard;
    }
}
