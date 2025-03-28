package it.polimi.galaxy_trucker.Model.tiles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class EngineTile extends ActivatableTile {

    public EngineTile(@JsonProperty("id") int id, @JsonProperty("connectors") Connector[] connectors, @JsonProperty("isDouble") boolean isDouble) {
        super(id,connectors, isDouble);
    }

    @Override
    public double getStrength() {
        if(getRotation() != 0) throw new IllegalArgumentException("Engine tiles cannot be rotated");
        if(isDouble()) {
            return(isPowered())? 2 : 0;
        }
        return 1;
    }

    @Override
    public String getTileType() {
        return (isDouble())? "Double Engine" : "Engine";
    }
}
