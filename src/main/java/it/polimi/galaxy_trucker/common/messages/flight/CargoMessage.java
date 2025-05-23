package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;
import it.polimi.galaxy_trucker.server.model.ShipBoard;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CargoMessage implements GameMessage {

    private String playerName;
    private Set<ShipBoard.CargoData> cargo;

    public CargoMessage() {}

    public CargoMessage(String playerName, Set<ShipBoard.CargoData> cargo) {
        this.playerName = playerName;
        this.cargo = cargo;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Set<ShipBoard.CargoData> getCargo() {
        return cargo;
    }

    @Override
    public String getType() {
        return "cargo";
    }
}
