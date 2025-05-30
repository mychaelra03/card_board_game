package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;
import it.polimi.galaxy_trucker.server.model.ShipBoard;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatteriesMessage implements GameMessage {

    private String playerName;
    private Set<ShipBoard.BatteriesData> batteries;

    public BatteriesMessage() {}

    public BatteriesMessage(String playerName, Set<ShipBoard.BatteriesData> batteries) {
        this.playerName = playerName;
        this.batteries = batteries;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Set<ShipBoard.BatteriesData> getBatteries() {
        return batteries;
    }

    @Override
    public String getType() {
        return "batteries";
    }
}
