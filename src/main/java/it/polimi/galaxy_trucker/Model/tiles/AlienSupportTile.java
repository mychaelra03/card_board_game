package it.polimi.galaxy_trucker.Model.tiles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class AlienSupportTile extends Tile {

    private final CrewType alienType;

    public AlienSupportTile(@JsonProperty("id") int id, @JsonProperty("connectors") Connector[] connectors, @JsonProperty("alienType") CrewType alienType) {
        super(id, connectors);
        if(alienType == CrewType.HUMAN) throw new IllegalArgumentException(alienType + " is a not a valid alien type.");
        this.alienType = alienType;
    }

    public CrewType getAlienColor() {
        return alienType;
    }

    @Override
    public String getTileType() {
        return "Alien Support";
    }
}