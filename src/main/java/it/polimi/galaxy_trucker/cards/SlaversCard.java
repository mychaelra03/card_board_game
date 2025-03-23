package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.Player;

public class SlaversCard extends AbstractEnemy {
    // Lose crew members if defeated.

    private final int rewardCredits;
    private final int crewLossOnDefeat;

    public SlaversCard(int ID, int level, int requiredCannonPower, int rewardCredits, int positionCost, int crewLossOnDefeat) {
        super(ID, level, requiredCannonPower, positionCost);
        if (rewardCredits < 0) throw new IllegalArgumentException("rewardCredits must be a positive number.");
        this.rewardCredits = rewardCredits;
        if (crewLossOnDefeat < 0) throw new IllegalArgumentException("crewLossOnDefeat must be a positive number.");
        this.crewLossOnDefeat = crewLossOnDefeat;
    }

    @Override
    public void giveReward(Player player) {

    }

    @Override
    public void applyPunishment(Player player) {

    }

    @Override
    public String getCardType() {
        return "Slavers";
    }
}
