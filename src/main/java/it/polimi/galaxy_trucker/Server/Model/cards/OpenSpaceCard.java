package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.tiles.EngineTile;

import java.util.List;

import static it.polimi.galaxy_trucker.GameModel.Direction.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class OpenSpaceCard extends Card {
    // Move forward based on engine strength.

    public OpenSpaceCard(@JsonProperty("id") int id, @JsonProperty("level") int level) {
        super(id, level);
    }

    @Override
    public void applyEffects(GameModel board) {
        List<Player> players = board.getSortedPlayers();
        for(Player player : players) {
            // TODO: should the player be forced to use a battery to not get eliminated?
            player.chooseTilesToActivate(EngineTile.class);
            int strength = (int)player.getCurrentStrength(EngineTile.class);
            if(strength == 0) {
                player.setEliminated(true);
                break;
            }
            board.movePlayer(player, strength, FORWARD);
        }
    }

    @Override
    public String getCardType() {
        return "Open Space";
    }
}
