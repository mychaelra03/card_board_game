package it.polimi.galaxy_trucker.server.model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.server.events.flight.CrewUpdateEvent;
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
        List<Player> players = board.getSortedPlayers();
        for(Player player : players) {
            player.applyEpidemicCard();
            dispatcher.fireEvent(new CrewUpdateEvent(player.getName(), player.getShipContent().crewData()));
        }
        setInputState(FINISHED);
    }


    @Override
    public String getCardType() {
        return "Epidemic";
    }
}
