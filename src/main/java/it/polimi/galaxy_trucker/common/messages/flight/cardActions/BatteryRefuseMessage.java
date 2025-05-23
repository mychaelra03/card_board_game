package it.polimi.galaxy_trucker.common.messages.flight.cardActions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.BatteryHandlerCard;
import it.polimi.galaxy_trucker.server.model.cards.Card;

import java.awt.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatteryRefuseMessage implements CardActionMessage {

    private String playerName;

    public BatteryRefuseMessage() {}

    public BatteryRefuseMessage(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public String getType() {
        return "refuse_battery";
    }


    @Override
    public void applyTo(Card card, Player player) {
        if (card instanceof BatteryHandlerCard c) {
            c.refuseBattery(player);
        } else {
            Logger.logError(getClass().getSimpleName() + " sent to invalid card: " + card.getClass().getSimpleName());
        }
    }
}
