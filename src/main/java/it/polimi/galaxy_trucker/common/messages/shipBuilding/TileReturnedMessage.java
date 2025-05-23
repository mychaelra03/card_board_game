package it.polimi.galaxy_trucker.common.messages.shipBuilding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TileReturnedMessage implements GameMessage {

    private int tileId;

    public TileReturnedMessage() {}

    public TileReturnedMessage(int tileId) {
       this.tileId = tileId;
    }

    public int getTileId() {
        return tileId;
    }

    @Override
    public String getType() {
        return "tile_returned";
    }
}
