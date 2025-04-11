package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.cards.projectile.Meteor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)

public class MeteorSwarmCard extends Card {
    // Rolling dice determines if meteors hit your ship.

    private final Meteor[] meteors;

    private List<Meteor> meteorQueue;

    public MeteorSwarmCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("meteors") Meteor[] meteors) {
        super(id, level);
        this.meteors = meteors;
    }

    @Override
    protected void onActivate() {
        this.meteorQueue = new ArrayList<>(Arrays.asList(meteors));
        applyFirstMeteor();
    }

    private void applyFirstMeteor() {
        List<Player> players = board.getSortedPlayers();
        int diceSum = GameModel.rollTwoDice();
        for(Player player : players) {
            player.applyProjectile(meteorQueue.getFirst(), diceSum);
        }
        meteorQueue.removeFirst();
    }

    //@Override
//    public void applyEffects() {
//        for(Meteor meteor : meteors) {
//            List<Player> players = board.getSortedPlayers();
//            int diceSum = GameModel.rollTwoDice();
//            for(Player player : players) {
//                player.applyProjectile(meteor, diceSum);
//            }
//        }
//    }

    @Override
    public String getCardType() {
        return "Meteor Swarm";
    }
}
