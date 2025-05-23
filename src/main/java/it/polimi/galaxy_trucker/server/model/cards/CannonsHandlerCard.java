package it.polimi.galaxy_trucker.server.model.cards;

import it.polimi.galaxy_trucker.server.model.Player;

import java.awt.*;
import java.util.List;

public interface CannonsHandlerCard {
    void activateCannons(Player caller, int verticalCannons, int rotatedCannons, List<Point> batteryTiles);
}