package it.polimi.galaxy_trucker.common.messages.shipBuilding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardsTakenMessage implements GameMessage {

    private String playerName;
    private int pileId;

    public CardsTakenMessage() {}

    public CardsTakenMessage(String playerName, int pileId) {
       this.playerName = playerName;
       this.pileId = pileId;
    }

    public String getPlayerName() {
       return playerName;
    }

    public int getPileId() {
        return pileId;
    }

    @Override
    public String getType() {
        return "cardsTaken";
    }
}
