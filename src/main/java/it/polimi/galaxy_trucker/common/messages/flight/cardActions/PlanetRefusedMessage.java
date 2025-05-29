package it.polimi.galaxy_trucker.common.messages.flight.cardActions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.Card;
import it.polimi.galaxy_trucker.server.model.cards.PlanetsCard;

/**
 * A message indicating that the player has refused to choose a planet
 * during a {@link PlanetsCard} event.
 * <p>
 * This message triggers the {@code refusePlanet} method on the {@code PlanetsCard},
 * allowing the game to handle the player's refusal accordingly.
 * </p>
 *
 * @see PlanetsCard
 * @see CardActionMessage
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanetRefusedMessage implements CardActionMessage {

    public PlanetRefusedMessage() {}

    @Override
    public String getType() {
        return "refuse_planet";
    }


    @Override
    public void applyTo(Card card, Player player) {
        if (card instanceof PlanetsCard c) {
            c.refusePlanet(player);
        } else {
            Logger.logError(getClass().getSimpleName() + " sent to invalid card: " + card.getClass().getSimpleName());
        }
    }
}
