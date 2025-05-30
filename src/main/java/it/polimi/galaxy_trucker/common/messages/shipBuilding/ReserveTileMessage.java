package it.polimi.galaxy_trucker.common.messages.shipBuilding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReserveTileMessage implements GameMessage {

    private int slotId;

    public ReserveTileMessage() {}

    public ReserveTileMessage(int slotId) {
       this.slotId = slotId;
    }

    public int getSlotId() {
        return slotId;
    }

    @Override
    public String getType() {
        return "reserve_tile";
    }
}
