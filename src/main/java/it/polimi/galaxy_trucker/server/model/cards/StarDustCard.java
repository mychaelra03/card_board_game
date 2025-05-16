package it.polimi.galaxy_trucker.server.model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.server.model.Player;

import java.util.List;

import static it.polimi.galaxy_trucker.server.model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class StarDustCard extends Card {
    // Players lose flight days based on exposed connectors.

    public StarDustCard(@JsonProperty("id")int id, @JsonProperty("level")int level) {
        super(id, level);
    }

    @Override
    protected void onActivate() {
        List<Player> playersReversed = board.getSortedPlayers().reversed();
        for(Player player : playersReversed) {
            int exposedConnectors = player.getExposedConnectorsCount();
            board.movePlayerBackward(player, exposedConnectors);
        }
        setInputState(FINISHED);
    }

    @Override
    public String getCardType() {
        return "Star Dust";
    }
}
