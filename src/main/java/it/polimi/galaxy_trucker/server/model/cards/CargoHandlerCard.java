package it.polimi.galaxy_trucker.server.model.cards;

import it.polimi.galaxy_trucker.server.model.Player;

import java.awt.*;

public interface CargoHandlerCard {
    void loadCargo(Player player, int blockIndex, Point tileCoords, int containerIndex);
    void unloadCargo(Player caller, Point tileCoords, int containerIndex);
    void confirmDone(Player caller);
}