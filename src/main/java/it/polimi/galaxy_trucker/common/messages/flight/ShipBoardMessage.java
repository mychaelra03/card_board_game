package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipBoardMessage implements GameMessage {

    private String playerName;
    private List<TileInfo> tiles;

    public ShipBoardMessage() {}

    public ShipBoardMessage(String playerName, List<TileInfo> tiles) {
        this.playerName = playerName;
        this.tiles = tiles;
    }

    public String getPlayerName() {
        return playerName;
    }

    public List<TileInfo> getTiles() {
        return tiles;
    }

    @Override
    public String getType() {
        return "shipBoard";
    }
}
