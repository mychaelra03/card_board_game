package it.polimi.galaxy_trucker.common.messages.flight.cardActions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.Card;
import it.polimi.galaxy_trucker.server.model.cards.CrewHandlerCard;

import java.awt.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoveMemberMessage implements CardActionMessage {

    private Point cabinCoords;

    public RemoveMemberMessage() {}

    public RemoveMemberMessage(Point cabinCoords) {
        this.cabinCoords = cabinCoords;
    }
    public Point getCabinCoords() {
        return cabinCoords;
    }

    @Override
    public String getType() {
        return "remove_member";
    }


    @Override
    public void applyTo(Card card, Player player) {
        if (card instanceof CrewHandlerCard c) {
            c.removeMemberFrom(player, cabinCoords);
        } else {
            Logger.logError(getClass().getSimpleName() + " sent to invalid card: " + card.getClass().getSimpleName());
        }
    }
}
