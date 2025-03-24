package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.GameBoard;
import it.polimi.galaxy_trucker.Player;

import java.util.List;

import static it.polimi.galaxy_trucker.GameBoard.Direction.*;

public class AbandonedShipCard extends Card {
    // Exchange crew members for credits.

    private final int crewToGiveUp;
    private final int rewardCredits;
    private final int positionCost;

    public AbandonedShipCard(int ID, int level, int crewToGiveUp, int rewardCredits, int positionCost) {
        super(ID, level);
        if (crewToGiveUp < 0) throw new IllegalArgumentException("crewToGiveUp must be a positive number.");
        this.crewToGiveUp = crewToGiveUp;
        if (rewardCredits < 0) throw new IllegalArgumentException("rewardCredits must be a positive number.");
        this.rewardCredits = rewardCredits;
        if (positionCost < 0) throw new IllegalArgumentException("travelCost must be a positive number.");
        this.positionCost = positionCost;
    }

    @Override
    public void applyEffects(GameBoard board) {
        List<Player> players = board.getSortedPlayers();
        for(Player player : players) {
            if(player.getCrewCount() < crewToGiveUp) continue;

            boolean accepted = player.acceptsCardEffect();

            if(accepted) {
                player.removeCrew(crewToGiveUp);
                player.addCredits(rewardCredits);
                board.movePlayer(player, positionCost, BACKWARD);
                break;
            }
        }
    }

    @Override
    public String getCardType() {
        return "Abandoned Ship";
    }
}
