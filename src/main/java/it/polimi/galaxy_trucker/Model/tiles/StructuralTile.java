package it.polimi.galaxy_trucker.Model.tiles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class StructuralTile extends Tile {

    public StructuralTile(@JsonProperty("id") int id, @JsonProperty("connectors") Connector[] connectors) {
        super(id, connectors);
    }

    @Override
    public String getTileType() {
        return "Structural Module";
    }
}
