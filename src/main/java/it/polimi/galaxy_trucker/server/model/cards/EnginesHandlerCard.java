package it.polimi.galaxy_trucker.server.model.cards;

import it.polimi.galaxy_trucker.server.model.Player;

import java.awt.*;
import java.util.List;

public interface EnginesHandlerCard {
    void activateEngines(Player caller, int activatedEngines, List<Point> batteryTiles);
}