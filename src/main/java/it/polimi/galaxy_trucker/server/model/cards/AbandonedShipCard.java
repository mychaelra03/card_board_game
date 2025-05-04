package it.polimi.galaxy_trucker.server.model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;

import java.awt.*;
import java.util.List;

import static it.polimi.galaxy_trucker.server.model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class AbandonedShipCard extends Card {
    // Exchange crew members for credits.

    private final int crewToGiveUp;
    private final int rewardCredits;
    private final int positionCost;

    private List<Player> playerQueue;
    private Player rewardedPlayer;
    private int remainingCrewToGiveUp;

    public AbandonedShipCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("crewToGiveUp") int crewToGiveUp,
            @JsonProperty("rewardCredits") int rewardCredits,
            @JsonProperty("positionCost") int positionCost) {
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
        setInputState(REWARD_DECISION);
        skipIneligiblePlayers();
    }

    public void accept(Player caller) {
        verifyInputState(REWARD_DECISION);
        verifyPlayerTurn(caller, playerQueue);
        if(caller.getCrewCount() <= crewToGiveUp) throw new IllegalStateException("Caller should have been skipped.");
        caller.giveCredits(rewardCredits);
        board.movePlayerBackward(caller, positionCost);
        playerQueue.clear();
        if(crewToGiveUp > 0) {
            this.rewardedPlayer = caller;
            this.remainingCrewToGiveUp = crewToGiveUp;
            setInputState(CREW_DECISION);
        }
        else setInputState(FINISHED);
    }

    public void removeMemberFrom(Player caller, Point cabinCoords, int index) {
        verifyInputState(CREW_DECISION);
        verifyCaller(caller, rewardedPlayer);
        if(remainingCrewToGiveUp <= 0) throw new RuntimeException("No more crew members are required to be removed.");
        caller.ensureCrewRemovedAt(cabinCoords, index);
        remainingCrewToGiveUp--;
        if(remainingCrewToGiveUp == 0) {
            setInputState(FINISHED);
        }
    }

    public void refuse(Player caller) {
        verifyInputState(REWARD_DECISION);
        verifyPlayerTurn(caller, playerQueue);
        playerQueue.removeFirst();
        skipIneligiblePlayers();
    }

    private void skipIneligiblePlayers() {
        verifyInputState(REWARD_DECISION);
        while(!playerQueue.isEmpty() && playerQueue.getFirst().getCrewCount() <= crewToGiveUp) {
            playerQueue.removeFirst();
        }
        if(playerQueue.isEmpty()) {
            setInputState(FINISHED);
        }
    }

    @Override
    public String getCardType() {
        return "Abandoned Ship";
    }
}
