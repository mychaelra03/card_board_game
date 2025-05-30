package it.polimi.galaxy_trucker.common.messages.shipBuilding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardsReturnedMessage implements GameMessage {

    private int pileId;

    public CardsReturnedMessage() {}

    public CardsReturnedMessage(int pileId) {
       this.pileId = pileId;
    }

    public int getPileId() {
        return pileId;
    }

    @Override
    public String getType() {
        return "cards_returned";
    }
}
