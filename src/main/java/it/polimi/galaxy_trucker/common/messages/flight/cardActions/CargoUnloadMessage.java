package it.polimi.galaxy_trucker.common.messages.flight.cardActions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.Card;
import it.polimi.galaxy_trucker.server.model.cards.CargoHandlerCard;

import java.awt.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CargoUnloadMessage implements CardActionMessage {

    private String playerName;
    private Point tileCoords;
    private int containerIndex;

    public CargoUnloadMessage() {}

    public CargoUnloadMessage(String playerName, Point tileCoords, int containerIndex) {
        this.playerName = playerName;
        this.tileCoords = tileCoords;
        this.containerIndex = containerIndex;
    }

    public String getPlayerName() {
        return playerName;
    }
    public Point getTileCoords() {
        return tileCoords;
    }
    public int getContainerIndex() {
        return containerIndex;
    }

    @Override
    public String getType() {
        return "unload_cargo";
    }


    @Override
    public void applyTo(Card card, Player player) {
        if (card instanceof CargoHandlerCard c) {
            c.unloadCargo(player, tileCoords, containerIndex);
        } else {
            Logger.logError(getClass().getSimpleName() + " sent to invalid card: " + card.getClass().getSimpleName());
        }
    }
}
