package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.Player;

import java.util.List;
import java.util.Queue;

import static it.polimi.galaxy_trucker.GameModel.Direction.*;
import static it.polimi.galaxy_trucker.Server.Model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class AbandonedShipCard extends Card {
    // Exchange crew members for credits.

    private final int crewToGiveUp;
    private final int rewardCredits;
    private final int positionCost;

    private List<Player> playerQueue;

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
        validatePlayerTurn(caller, playerQueue);
        caller.removeCrew(crewToGiveUp);
        caller.giveCredits(rewardCredits);
        board.movePlayer(caller, positionCost, BACKWARD);
        playerQueue.clear();
        setInputState(FINISHED);
    }

    public void refuse(Player caller) {
        verifyInputState(REWARD_DECISION);
        validatePlayerTurn(caller, playerQueue);
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
