package it.polimi.galaxy_trucker.tiles;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatteriesTile extends Tile {

    private final int capacity;
    private int storedBatteries;

    @JsonCreator
    public BatteriesTile(@JsonProperty("id") int id, @JsonProperty("connectors") Connector[] connectors,@JsonProperty("capacity") int capacity) {
        super(id,connectors);
        if (capacity < 2 || capacity > 3) throw new IllegalArgumentException("Battery Component must have capacity of 2 or 3.");
        this.capacity = capacity;
        this.storedBatteries = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getStoredBatteries() {
        return storedBatteries;
    }

//    public boolean addBattery() {
//        if (storedBatteries < capacity) {
//            storedBatteries++;
//            return true;
//        }
//        return false;
//    }

    public boolean removeBattery() {
        if (storedBatteries > 0) {
            storedBatteries--;
            return true;
        }
        return false;
    }

    @Override
    public String getTileType() {
        return "Battery Container";
    }
}
