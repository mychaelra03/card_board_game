package it.polimi.galaxy_trucker.tiles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CannonTile extends Tile {

    private final boolean isDouble;
    private boolean isPowered;

    public CannonTile(@JsonProperty("id")int ID,@JsonProperty("connectors") Connector[] connectors ,@JsonProperty("isDouble") boolean isDouble) {
        super(ID, connectors);
        this.isDouble = isDouble;
        this.isPowered = false;
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
            if(!isPowered) return 0;
            return (getRotation() == 0) ? 2 : 1;
        }
        return (getRotation() == 0) ? 1 : 0.5;
    }

    @Override
    public String getTileType() {
        return (isDouble)? "Double Cannon" : "Cannon";
    }
}
