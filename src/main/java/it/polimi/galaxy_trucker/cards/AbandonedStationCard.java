package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.GameBoard;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.tiles.BlockType;

import java.util.List;

import static it.polimi.galaxy_trucker.GameBoard.Direction.BACKWARD;

public class AbandonedStationCard extends Card {
    // Collect goods if you have enough crew.

    private final int requiredCrew;
    private final BlockType[] rewardCargo;
    private final int positionCost;

    public AbandonedStationCard(int ID, int level, int requiredCrew, BlockType[] rewardCargo, int positionCost) {
        super(ID, level);
        if (requiredCrew < 0) throw new IllegalArgumentException("positionCost must be a positive number.");
        this.requiredCrew = requiredCrew;
        this.rewardCargo = rewardCargo;
        if (positionCost < 0) throw new IllegalArgumentException("positionCost must be a positive number.");
        this.positionCost = positionCost;
    }

    @Override
    public void applyEffects(GameBoard board) {
        List<Player> players = board.getSortedPlayers();
        for(Player player : players) {
            if(player.getCrewCount() < requiredCrew) continue;

            boolean accepted = player.acceptsCardEffect();

            if(accepted) {
                player.giveCargoReward(rewardCargo);
                board.movePlayer(player, positionCost, BACKWARD);
                break;
            }
        }
    }

    @Override
    public String getCardType() {
        return "Abandoned Station";
    }
}
