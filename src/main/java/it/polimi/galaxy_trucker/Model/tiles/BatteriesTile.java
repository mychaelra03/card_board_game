package it.polimi.galaxy_trucker.Model.tiles;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class BatteriesTile extends Tile {

    private final int capacity;
    private int storedBatteries;

    public BatteriesTile(@JsonProperty("id") int id, @JsonProperty("connectors") Connector[] connectors, @JsonProperty("capacity") int capacity) {
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

    public boolean isFull() {
        return storedBatteries == capacity;
    }

    public boolean removeBattery() {
        if(storedBatteries <= 0) return false;
        storedBatteries--;
        return true;
    }
    public boolean addBattery() {
        if (storedBatteries >= capacity) return false;
        storedBatteries++;
        return true;
    }

    @Override
    public String getTileType() {
        return "Battery Container";
    }
}
