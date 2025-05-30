package it.polimi.galaxy_trucker.server.events.flight;

import it.polimi.galaxy_trucker.server.events.EventModel;

import java.awt.*;
import java.util.Set;

public class AlienCabinTilesEvent extends EventModel {

    private final String playerName;
    private final Set<Point> cabinTiles;

    public AlienCabinTilesEvent(String playerName, Set<Point> cabinTiles) {
        this.playerName = playerName;
        this.cabinTiles = cabinTiles;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Set<Point> getCabinTiles() {
        return cabinTiles;
    }
}

