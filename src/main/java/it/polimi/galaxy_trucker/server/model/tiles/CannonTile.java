package it.polimi.galaxy_trucker.server.model.tiles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CannonTile extends ActivatableTile {

    public CannonTile(@JsonProperty("id") int id, @JsonProperty("connectors") Connector[] connectors, @JsonProperty("isDouble") boolean isDouble) {
        super(id, connectors, isDouble);
    }

    public static double getStrength(int rotation, boolean isDouble) {
        if(isDouble) return (rotation == 0) ? 2 : 1;
        return (rotation == 0) ? 1 : 0.5;
    }

    @Override
    public String getTileType() {
        return isDouble()? "Double Cannon" : "Cannon";
    }
}
