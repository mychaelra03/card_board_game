package it.polimi.galaxy_trucker.common.messages.flight.cardActions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.BatteryHandlerCard;
import it.polimi.galaxy_trucker.server.model.cards.Card;
import it.polimi.galaxy_trucker.server.model.cards.CargoHandlerCard;

import java.awt.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatteryUseMessage implements CardActionMessage {

    private String playerName;
    private Point batteryCoords;

    public BatteryUseMessage() {}

    public BatteryUseMessage(String playerName, Point batteryCoords) {
        this.playerName = playerName;
        this.batteryCoords = batteryCoords;
    }

    public String getPlayerName() {
        return playerName;
    }
    public Point getBatteryCoords() {
        return batteryCoords;
    }

    @Override
    public String getType() {
        return "use_battery";
    }


    @Override
    public void applyTo(Card card, Player player) {
        if (card instanceof BatteryHandlerCard c) {
            c.useBattery(player, batteryCoords);
        } else {
            Logger.logError(getClass().getSimpleName() + " sent to invalid card: " + card.getClass().getSimpleName());
        }
    }
}
