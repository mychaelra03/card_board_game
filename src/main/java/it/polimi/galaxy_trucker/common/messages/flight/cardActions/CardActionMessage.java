package it.polimi.galaxy_trucker.common.messages.flight.cardActions;

import it.polimi.galaxy_trucker.common.messages.GameMessage;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.Card;

public interface CardActionMessage extends GameMessage {
    void applyTo(Card card, Player player);
}
