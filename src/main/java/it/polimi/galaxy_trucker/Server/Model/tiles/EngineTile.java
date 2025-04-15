package it.polimi.galaxy_trucker.Server.Model.tiles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class EngineTile extends ActivatableTile {

    public EngineTile(@JsonProperty("id") int id, @JsonProperty("connectors") Connector[] connectors, @JsonProperty("isDouble") boolean isDouble) {
        super(id, connectors, isDouble);
    }

    public static double getStrength(int rotation, boolean isDouble) {
        if(rotation != 0) throw new IllegalArgumentException("Engine tiles cannot be rotated");
        return isDouble? 2 : 1;
    }

    @Override
    public String getTileType() {
        return isDouble()? "Double Engine" : "Engine";
    }
}
