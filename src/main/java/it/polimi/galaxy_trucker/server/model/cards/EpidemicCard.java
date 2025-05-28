package it.polimi.galaxy_trucker.server.model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.server.events.flight.cards.CardStatusEvent;
import it.polimi.galaxy_trucker.server.model.Player;

import java.util.List;

import static it.polimi.galaxy_trucker.server.model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class EpidemicCard extends Card {
    // Lose crew from connected cabins.

    public EpidemicCard(@JsonProperty("id") int id, @JsonProperty("level") int level) {
        super(id, level);
    }

    @Override
    protected void onActivate() {
        dispatcher.fireEvent(new CardStatusEvent(CardStatusEvent.CardStatus.EPIDEMIC));
        List<Player> players = board.getSortedPlayers();
        for(Player player : players) {
            player.applyEpidemicCard();
        }
        setInputState(FINISHED);
    }


    @Override
    public String getCardType() {
        return "Epidemic";
    }
}
