package it.polimi.galaxy_trucker.common.messages.flight.cardActions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.Card;
import it.polimi.galaxy_trucker.server.model.cards.EnginesHandlerCard;

import java.awt.*;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivateEnginesMessage implements CardActionMessage {

    private int activatedEngines;
    private List<Point> batteryTiles;

    public ActivateEnginesMessage() {}

    public ActivateEnginesMessage(int activatedEngines, List<Point> batteryTiles) {
        this.activatedEngines = activatedEngines;
        this.batteryTiles = batteryTiles;
    }

    public int getActivatedEngines() {
        return activatedEngines;
    }
    public List<Point> getBatteryTiles() {
        return batteryTiles;
    }

    @Override
    public String getType() {
        return "activate_engines";
    }


    @Override
    public void applyTo(Card card, Player player) {
        if (card instanceof EnginesHandlerCard c) {
            c.activateEngines(player, activatedEngines, batteryTiles);
        } else {
            Logger.logError(getClass().getSimpleName() + " sent to invalid card: " + card.getClass().getSimpleName());
        }
    }
}
