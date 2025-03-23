package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.Player;

public class PiratesCard extends AbstractEnemy {
    // Combat encounter, win to gain credits, lose to lose crew.

    private final int rewardCredits;
    private final CannonShot[] cannonShots;

    public PiratesCard(int ID, int level, int requiredCannonPower, int rewardCredits, int positionCost, CannonShot[] cannonShots) {
        super(ID, level, requiredCannonPower, positionCost);
        if (rewardCredits < 0) throw new IllegalArgumentException("rewardCredits must be a positive number.");
        this.rewardCredits = rewardCredits;
        this.cannonShots = cannonShots;
    }

    @Override
    public void giveReward(Player player) {

    }

    @Override
    public void applyPunishment(Player player) {

    }

    @Override
    public String getCardType() {
        return "Pirates";
    }
}
