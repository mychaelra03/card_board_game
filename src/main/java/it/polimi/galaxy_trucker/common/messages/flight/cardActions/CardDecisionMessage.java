package it.polimi.galaxy_trucker.common.messages.flight.cardActions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.Card;
import it.polimi.galaxy_trucker.server.model.cards.DecisionHandlerCard;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardDecisionMessage implements CardActionMessage {

    private String playerName;
    private Boolean accepted;

    public CardDecisionMessage() {}

    public CardDecisionMessage(String playerName, Boolean accepted) {
        this.playerName = playerName;
        this.accepted = accepted;
    }

    public String getPlayerName() {
        return playerName;
    }
    public Boolean getAccepted() {
        return accepted;
    }

    @Override
    public String getType() {
        return "card_decision";
    }


    @Override
    public void applyTo(Card card, Player player) {
        if (card instanceof DecisionHandlerCard c) {
            if (accepted) {
                c.accept(player);
            } else {
                c.refuse(player);
            }
        } else {
            Logger.logError(getClass().getSimpleName() + " sent to invalid card: " + card.getClass().getSimpleName());
        }
    }
}
