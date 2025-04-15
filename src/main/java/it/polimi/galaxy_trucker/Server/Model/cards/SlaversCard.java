package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;

import static it.polimi.galaxy_trucker.Server.Model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class SlaversCard extends AbstractEnemy {
    // Combat encounter, win to gain credits, lose to lose crew members.

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
    protected void giveReward(Player player) {
        verifyInputState(NONE);
        player.giveCredits(rewardCredits);
        setInputState(FINISHED);
    }

    @Override
    protected void applyPunishment(Player player) {
        verifyInputState(NONE);
        player.removeCrew(crewLossOnDefeat);
        handleNextPlayer();
    }

    @Override
    public String getCardType() {
        return "Slavers";
    }
}
