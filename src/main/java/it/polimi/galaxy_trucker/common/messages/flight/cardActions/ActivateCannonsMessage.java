package it.polimi.galaxy_trucker.common.messages.flight.cardActions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.CannonsHandlerCard;
import it.polimi.galaxy_trucker.server.model.cards.Card;

import java.awt.*;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivateCannonsMessage implements CardActionMessage {

    private int verticalCannons;
    private int rotatedCannons;
    private List<Point> batteryTiles;

    public ActivateCannonsMessage() {}

    public ActivateCannonsMessage(int verticalCannons, int rotatedCannons, List<Point> batteryTiles) {
        this.verticalCannons = verticalCannons;
        this.rotatedCannons = rotatedCannons;
        this.batteryTiles = batteryTiles;
    }

    public int getVerticalCannons() {
        return verticalCannons;
    }
    public int getRotatedCannons() {
        return rotatedCannons;
    }
    public List<Point> getBatteryTiles() {
        return batteryTiles;
    }

    @Override
    public String getType() {
        return "activate_cannons";
    }


    @Override
    public void applyTo(Card card, Player player) {
        if (card instanceof CannonsHandlerCard c) {
            c.activateCannons(player, verticalCannons, rotatedCannons, batteryTiles);
        } else {
            Logger.logError(getClass().getSimpleName() + " sent to invalid card: " + card.getClass().getSimpleName());
        }
    }
}
