package it.polimi.galaxy_trucker.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameBoard;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.cards.projectile.Meteor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)

public class MeteorSwarmCard extends Card {
    // Rolling dice determines if meteors hit your ship.

    private final Meteor[] meteors;

    public MeteorSwarmCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("meteors") Meteor[] meteors) {
        super(id, level);
        this.meteors = meteors;
    }

    @Override
    public void applyEffects(GameBoard board) {
        for(Meteor meteor : meteors) {
            List<Player> players = board.getSortedPlayers();
            int diceSum = GameBoard.rollTwoDice();
            for(Player player : players) {
                player.applyProjectile(meteor, diceSum);
            }
        }
    }

    @Override
    public String getCardType() {
        return "Meteor Swarm";
    }
}
