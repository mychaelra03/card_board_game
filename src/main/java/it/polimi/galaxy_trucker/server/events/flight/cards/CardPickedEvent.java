package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.cards.Card;

public class CardPickedEvent extends EventModel {

    private final Card card;

    public CardPickedEvent(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}

