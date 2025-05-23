package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;
import it.polimi.galaxy_trucker.server.model.ShipBoard;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditsMessage implements GameMessage {

    private String playerName;
    private int credits;

    public CreditsMessage() {}

    public CreditsMessage(String playerName, int credits) {
        this.playerName = playerName;
        this.credits = credits;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getCredits() {
        return credits;
    }

    @Override
    public String getType() {
        return "credits";
    }
}
