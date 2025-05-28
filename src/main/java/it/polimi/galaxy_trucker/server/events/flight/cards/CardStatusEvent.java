package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;

public class CardStatusEvent extends EventModel {

    public enum CardStatus {
        RULE_CREW, RULE_CANNON, RULE_ENGINE, COMBAT_SKIPPED, EPIDEMIC, STARDUST
    }

    private final CardStatus status;

    public CardStatusEvent(CardStatus status) {
        this.status = status;
    }

    public CardStatus getStatus() {
        return status;
    }
}
