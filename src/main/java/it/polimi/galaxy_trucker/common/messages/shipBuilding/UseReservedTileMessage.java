package it.polimi.galaxy_trucker.common.messages.shipBuilding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UseReservedTileMessage implements GameMessage {

    private int slotId;

    public UseReservedTileMessage() {}

    public UseReservedTileMessage(int slotId) {
       this.slotId = slotId;
    }

    public int getSlotId() {
        return slotId;
    }

    @Override
    public String getType() {
        return "use_reserved_tile";
    }
}
