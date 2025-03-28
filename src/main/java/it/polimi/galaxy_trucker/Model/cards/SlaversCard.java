package it.polimi.galaxy_trucker.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;

@JsonIgnoreProperties(ignoreUnknown = true)

public class SlaversCard extends AbstractEnemy {
    // Lose crew members if defeated.

    private final int rewardCredits;
    private final int crewLossOnDefeat;

    public SlaversCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("requiredCannonPower") int requiredCannonPower,
            @JsonProperty("rewardCredits") int rewardCredits,
            @JsonProperty("positionCost") int positionCost,
            @JsonProperty("crewLossOnDefeat") int crewLossOnDefeat) {
        super(id, level, requiredCannonPower, positionCost);
        if (rewardCredits < 0) throw new IllegalArgumentException("rewardCredits must be a positive number.");
        this.rewardCredits = rewardCredits;
        if (crewLossOnDefeat < 0) throw new IllegalArgumentException("crewLossOnDefeat must be a positive number.");
        this.crewLossOnDefeat = crewLossOnDefeat;
    }

    @Override
    public void giveReward(Player player) {
        player.giveCredits(rewardCredits);
    }

    @Override
    public void applyPunishment(Player player) {
        player.removeCrew(crewLossOnDefeat);
    }

    @Override
    public String getCardType() {
        return "Slavers";
    }
}
