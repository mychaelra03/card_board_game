package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.tiles.CrewType;

import java.awt.*;

import static it.polimi.galaxy_trucker.Server.Model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class SlaversCard extends AbstractEnemy {
    // Combat encounter, win to gain credits, lose to lose crew members.

    private final int rewardCredits;
    private final int crewLossOnDefeat;

    private Player punishedPlayer;
    private int remainingCrewToGiveUp;

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
        if(crewLossOnDefeat > 0) {
            this.punishedPlayer = player;
            this.remainingCrewToGiveUp = crewLossOnDefeat;
            setInputState(CREW_DECISION);
        }
        else handleNextPlayer();
    }

    public void removeMemberFrom(Player caller, Point cabinCoords, int index) {
        verifyInputState(CREW_DECISION);
        verifyCaller(caller, punishedPlayer);
        if(remainingCrewToGiveUp <= 0) throw new RuntimeException("No more crew members are required to be removed.");
        caller.ensureCrewRemovedAt(cabinCoords, index);
        remainingCrewToGiveUp--;
        if(remainingCrewToGiveUp == 0) {
            setInputState(NONE);
            handleNextPlayer();
        }
    }

    @Override
    public String getCardType() {
        return "Slavers";
    }
}
