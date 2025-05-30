package it.polimi.galaxy_trucker.common.messages.flight.cardActions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.Card;
import it.polimi.galaxy_trucker.server.model.cards.PlanetsCard;

/**
 * A message representing the player's choice of a planet during a {@link PlanetsCard} event.
 * <p>
 * This message includes the index of the planet the player has selected, which is passed
 * to the card logic when applied.
 * </p>
 *
 * <p>
 * When executed, the message invokes the {@code choosePlanet} method of the corresponding
 * {@code PlanetsCard}, applying the player's decision to the game state.
 * </p>
 *
 * @see PlanetsCard
 * @see CardActionMessage
 */
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
