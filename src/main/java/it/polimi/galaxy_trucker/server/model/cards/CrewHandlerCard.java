package it.polimi.galaxy_trucker.server.model.cards;

import it.polimi.galaxy_trucker.server.model.Player;

import java.awt.*;

public interface CrewHandlerCard {
    void removeMemberFrom(Player caller, Point cabinCoords);
}