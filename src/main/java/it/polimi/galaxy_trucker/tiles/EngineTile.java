package it.polimi.galaxy_trucker.tiles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EngineTile extends Tile {

    private final boolean isDouble;
    private boolean isPowered;

    @JsonCreator
    public EngineTile(@JsonProperty("id") int id,@JsonProperty("connectors") Connector[] connectors ,@JsonProperty("isDouble") boolean isDouble) {
        super(id,connectors);
        this.isDouble = isDouble;
        this.isPowered = false;

    }


    @Override
    public void setRotation(int rotation) {
        // TODO: Remove this if the checks are done after the ship is built.
        if(rotation != 0)  throw new IllegalArgumentException("Engine tiles cannot be rotated");
    }

    public boolean isDouble() {
        return isDouble;
    }
    public void setPowered(boolean isPowered) {
        this.isPowered = isPowered;
    }
    public boolean isPowered() {
        return isPowered;
    }

    public double getStrength() {
        if(isDouble) {
            return(isPowered)? 2 : 0;
        }
        return 1;
    }

    @Override
    public String getTileType() {
        return (isDouble)? "Double Engine" : "Engine";
    }
}
