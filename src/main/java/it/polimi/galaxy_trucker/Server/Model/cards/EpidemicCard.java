package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;

import java.util.List;

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
        }
    }

    @Override
    public String getCardType() {
        return "Epidemic";
    }
}
