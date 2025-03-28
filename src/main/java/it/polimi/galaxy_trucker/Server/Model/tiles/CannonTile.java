package it.polimi.galaxy_trucker.Server.Model.tiles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CannonTile extends ActivatableTile {

    public CannonTile(@JsonProperty("id") int id, @JsonProperty("connectors") Connector[] connectors, @JsonProperty("isDouble") boolean isDouble) {
        super(id, connectors, isDouble);
    }

    @Override
    public double getStrength() {
        if(isDouble()) {
            if(!isPowered()) return 0;
            return (getRotation() == 0) ? 2 : 1;
        }
        return (getRotation() == 0) ? 1 : 0.5;
    }

    @Override
    public String getTileType() {
        return (isDouble())? "Double Cannon" : "Cannon";
    }
}
