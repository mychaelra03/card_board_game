package it.polimi.galaxy_trucker.tiles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StructuralTile extends Tile {

    @JsonCreator
    public StructuralTile(@JsonProperty("id") int ID,@JsonProperty("connectors") Connector[] connectors) {
        super(ID, connectors);
    }

    @Override
    public String getTileType() {
        return "Structural Module";
    }
}
