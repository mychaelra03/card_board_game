package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.tiles.BlockType;

public class SmugglersCard extends AbstractEnemy {
    // Combat encounter, win to gain goods, lose to lose goods.

    private final BlockType[] rewardCargo;
    private final int cargoLossOnDefeat;

    public SmugglersCard(int ID, int level, int requiredCannonPower, BlockType[] rewardCargo, int positionCost, int cargoLossOnDefeat) {
        super(ID, level, requiredCannonPower, positionCost);
        this.rewardCargo = rewardCargo;
        if (cargoLossOnDefeat < 0) throw new IllegalArgumentException("LossOnDefeat must be a positive number.");
        this.cargoLossOnDefeat = cargoLossOnDefeat;
    }

    @Override
    public void giveReward(Player player) {

    }

    @Override
    public void applyPunishment(Player player) {

    }

    @Override
    public String getCardType() {
        return "Smugglers";
    }
}
