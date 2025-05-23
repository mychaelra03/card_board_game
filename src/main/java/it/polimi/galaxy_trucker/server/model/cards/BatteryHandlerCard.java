package it.polimi.galaxy_trucker.server.model.cards;

import it.polimi.galaxy_trucker.server.model.Player;

import java.awt.*;

public interface BatteryHandlerCard {
    void useBattery(Player caller, Point batteryCoords);
    void refuseBattery(Player caller);
}