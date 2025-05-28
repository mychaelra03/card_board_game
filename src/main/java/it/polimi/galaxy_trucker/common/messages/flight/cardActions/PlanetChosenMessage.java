package it.polimi.galaxy_trucker.common.messages.flight.cardActions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.Card;
import it.polimi.galaxy_trucker.server.model.cards.PlanetsCard;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanetChosenMessage implements CardActionMessage {

    private int chosenPlanet;

    public PlanetChosenMessage() {}

    public PlanetChosenMessage(int chosenPlanet) {
        this.chosenPlanet = chosenPlanet;
    }

    public int getChosenPlanet() {
        return chosenPlanet;
    }

    @Override
    public String getType() {
        return "choose_planet";
    }


    @Override
    public void applyTo(Card card, Player player) {
        if (card instanceof PlanetsCard c) {
            c.choosePlanet(player, chosenPlanet);
        } else {
            Logger.logError(getClass().getSimpleName() + " sent to invalid card: " + card.getClass().getSimpleName());
        }
    }
}
