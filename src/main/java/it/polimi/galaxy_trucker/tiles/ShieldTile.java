package it.polimi.galaxy_trucker.tiles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShieldTile extends Tile {

    @JsonCreator
    public ShieldTile(@JsonProperty("id") int id,@JsonProperty("connectors") Connector[] connectors) {
        super(id, connectors);
    }

    @Override
    public String getTileType() {
        return "Shield";
    }
}
