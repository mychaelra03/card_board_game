package it.polimi.galaxy_trucker.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpidemicCard extends Card {
    // Lose crew from connected cabins.

    @JsonCreator
    public EpidemicCard(@JsonProperty("id") int id,@JsonProperty("level") int level) {
        super(id, level);
    }

    @Override
    public void applyEffects(GameModel board) {
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
