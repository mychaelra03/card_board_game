package it.polimi.galaxy_trucker.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;

import java.util.List;

import static it.polimi.galaxy_trucker.GameModel.Direction.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class StarDustCard extends Card {
    // Players lose flight days based on exposed connectors.

    public StarDustCard(@JsonProperty("id")int id, @JsonProperty("level")int level) {
        super(id, level);
    }

    @Override
    public void applyEffects(GameModel board) {
        List<Player> playersReversed = board.getSortedPlayers().reversed();
        for(Player player : playersReversed) {
            int exposedConnectors = player.getExposedConnectorsCount();
            board.movePlayer(player, exposedConnectors, BACKWARD);
        }
    }

    @Override
    public String getCardType() {
        return "Star Dust";
    }
}
