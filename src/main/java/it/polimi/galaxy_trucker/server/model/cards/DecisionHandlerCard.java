package it.polimi.galaxy_trucker.server.model.cards;

import it.polimi.galaxy_trucker.server.model.Player;

import java.awt.*;

public interface DecisionHandlerCard {
    void accept(Player caller);
    void refuse(Player caller);
}