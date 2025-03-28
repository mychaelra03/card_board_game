package it.polimi.galaxy_trucker.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Model.cards.projectile.CannonShot;

@JsonIgnoreProperties(ignoreUnknown = true)

public class PiratesCard extends AbstractEnemy {
    // Combat encounter, win to gain credits, lose to lose crew.

    private final int rewardCredits;
    private final CannonShot[] cannonShots;

    public PiratesCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("requiredCannonPower") int requiredCannonPower,
            @JsonProperty("rewardCredits") int rewardCredits,
            @JsonProperty("positionCost") int positionCost,
            @JsonProperty("cannonShots") CannonShot[] cannonShots) {
        super(id, level, requiredCannonPower, positionCost);
        if (rewardCredits < 0) throw new IllegalArgumentException("rewardCredits must be a positive number.");
        this.rewardCredits = rewardCredits;
        this.cannonShots = cannonShots;
    }

    @Override
    public void giveReward(Player player) {
        player.giveCredits(rewardCredits);
    }

    @Override
    public void applyPunishment(Player player) {
        for(CannonShot shot : cannonShots) {
            int diceSum = GameModel.rollTwoDice();
            player.applyProjectile(shot, diceSum);
        }
    }

    @Override
    public String getCardType() {
        return "Pirates";
    }
}
