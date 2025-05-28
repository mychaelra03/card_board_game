package it.polimi.galaxy_trucker.common.messages.flight.cardActions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.Card;
import it.polimi.galaxy_trucker.server.model.cards.CargoHandlerCard;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CargoDoneMessage implements CardActionMessage {

    private String playerName;

    public CargoDoneMessage() {}

    public CargoDoneMessage(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public String getType() {
        return "cargo_done";
    }


    @Override
    public void applyTo(Card card, Player player) {
        if (card instanceof CargoHandlerCard c) {
            c.confirmDone(player);
        } else {
            Logger.logError(getClass().getSimpleName() + " sent to invalid card: " + card.getClass().getSimpleName());
        }
    }
}
