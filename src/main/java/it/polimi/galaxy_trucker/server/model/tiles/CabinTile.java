package it.polimi.galaxy_trucker.server.model.tiles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import static it.polimi.galaxy_trucker.server.model.tiles.CrewType.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CabinTile extends Tile {

    private boolean canHostBrownAlien = false;
    private boolean canHostPurpleAlien = false;
    private CrewType crew;

    public CabinTile(@JsonProperty("id") int id, @JsonProperty("connectors") Connector[] connectors) {
        super(id, connectors);
        this.crew = DOUBLE_HUMAN;
    }

    public CrewType getCrew() {
        return crew;
    }

    public void setCrewType(CrewType type) {
        if (type.isAlien()) {
            if (type == CrewType.BROWN_ALIEN && !canHostBrownAlien) {
                throw new IllegalArgumentException("Can't host brown alien");
            }
            if (type == CrewType.PURPLE_ALIEN && !canHostPurpleAlien) {
                throw new IllegalArgumentException("Can't host purple alien");
            }
        }
        this.crew = type;
    }

    public void allowAlienType(CrewType alienType, boolean alienHabitable) {
        if(!alienType.isAlien()) throw new IllegalArgumentException(alienType + " is not an alien type.");
        if(alienType == BROWN_ALIEN) canHostBrownAlien = alienHabitable;
        else if(alienType == PURPLE_ALIEN) canHostPurpleAlien = alienHabitable;
        if(!alienHabitable && hasAlien(alienType)) crew = NONE;
    }
    public boolean hasAlien(CrewType alienType) {
        if(!alienType.isAlien()) throw new IllegalArgumentException(alienType + " is not an alien type.");
        return crew == alienType;
    }
    public boolean canHostAlienType(CrewType alienType) {
        if(!alienType.isAlien()) throw new IllegalArgumentException(alienType + " is not an alien type.");
        return (alienType == BROWN_ALIEN)? canHostBrownAlien : canHostPurpleAlien;
    }

    public boolean hasCrew() {
        return crew != CrewType.NONE;
    }

    public int getHumanCount() {
        return crew.humanCount();
    }

    public CrewType removeMember() {
        CrewType removed = crew;

        if(crew == DOUBLE_HUMAN) crew = SINGLE_HUMAN;
        else crew = NONE;

        return removed;
    }

    @Override
    public String getTileType() {
        return "Cabin";
    }
}