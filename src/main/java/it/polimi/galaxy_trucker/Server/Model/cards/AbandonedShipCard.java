package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;

import java.util.List;
import java.util.Queue;

import static it.polimi.galaxy_trucker.GameModel.Direction.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class AbandonedShipCard extends Card {
    // Exchange crew members for credits.

    private final int crewToGiveUp;
    private final int rewardCredits;
    private final int positionCost;

    private List<Player> playerQueue;

    public AbandonedShipCard(@JsonProperty("id")int id, @JsonProperty("level")int level, @JsonProperty("crewToGiveUp")int crewToGiveUp, @JsonProperty("rewardCredits")int rewardCredits, @JsonProperty("positionCost")int positionCost) {
        super(id, level);
        if (crewToGiveUp < 0) throw new IllegalArgumentException("crewToGiveUp must be a positive number.");
        this.crewToGiveUp = crewToGiveUp;
        if (rewardCredits < 0) throw new IllegalArgumentException("rewardCredits must be a positive number.");
        this.rewardCredits = rewardCredits;
        if (positionCost < 0) throw new IllegalArgumentException("travelCost must be a positive number.");
        this.positionCost = positionCost;
    }

    @Override
    protected void onActivate() {
        this.playerQueue = board.getSortedPlayers();
        askFirstPlayer();
    }

    private void askFirstPlayer() {
        while(!playerQueue.isEmpty() && playerQueue.getFirst().getCrewCount() < crewToGiveUp) {
            playerQueue.removeFirst();
        }
        if(!playerQueue.isEmpty()) {
            Player player = playerQueue.getFirst();
            // TODO
        }
    }

    // TODO: merge ship and station?
    public void accept() {
        if(playerQueue.isEmpty()) throw new IllegalStateException("No player available to make a choice.");
        Player player = playerQueue.getFirst();
        player.removeCrew(crewToGiveUp);
        player.giveCredits(rewardCredits);
        board.movePlayer(player, positionCost, BACKWARD);
    }

    public void refuse() {
        playerQueue.remove(playerQueue.getFirst());
        askFirstPlayer();
    }

    //@Override
//    public void applyEffects() {
//        List<Player> players = board.getSortedPlayers();
//        for(Player player : players) {
//            if(player.getCrewCount() < crewToGiveUp) continue;
//
//            boolean accepted = player.acceptsCardEffect();
//
//            if(accepted) {
//                player.removeCrew(crewToGiveUp);
//                player.giveCredits(rewardCredits);
//                board.movePlayer(player, positionCost, BACKWARD);
//                break;
//            }
//        }
//    }

    @Override
    public String getCardType() {
        return "Abandoned Ship";
    }
}
