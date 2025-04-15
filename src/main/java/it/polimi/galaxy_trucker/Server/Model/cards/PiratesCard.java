package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.cards.projectile.CannonShot;
import it.polimi.galaxy_trucker.Server.Model.cards.projectile.Projectile;

import java.awt.*;
import java.util.*;
import java.util.List;

import static it.polimi.galaxy_trucker.Server.Model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class PiratesCard extends AbstractEnemy {
    // Combat encounter, win to gain credits, lose to get shot.

    private final int rewardCredits;
    private final CannonShot[] cannonShots;

    private List<CannonShot> shotQueue;
    private Player punishedPlayer;
    private CannonShot currentShot;
    private int currentPathIndex;

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
    protected void giveReward(Player player) {
        verifyInputState(NONE);
        player.giveCredits(rewardCredits);
        setInputState(FINISHED);
    }

    @Override
    protected void applyPunishment(Player player) {
        verifyInputState(NONE);
        this.punishedPlayer = player;
        this.shotQueue = new LinkedList<>(Arrays.asList(cannonShots));
        applyShot();
    }

    private void applyShot() {
        verifyInputState(NONE);
        if(shotQueue.isEmpty()) {
            handleNextPlayer();
            return;
        }
        this.currentShot = shotQueue.getFirst();
        int[] dice = GameModel.rollTwoDice();
        this.currentPathIndex = Projectile.diceSumToIndex(currentShot.getSource(), dice[0] + dice[1]);

        boolean isPlayerAtRisk = currentShot.evaluateDefense(punishedPlayer, currentPathIndex);

        if(isPlayerAtRisk) {
            setInputState(BATTERY_DECISION);
        }
        else handleNextShot();
    }

    private void handleNextShot() {
        shotQueue.removeFirst();
        applyShot();
    }

    public void useBattery(Player caller, Point batteryCoords) {
        verifyInputState(BATTERY_DECISION);
        if(caller != punishedPlayer) throw new IllegalArgumentException("It is not " + caller.getName() + "'s turn. It is currently " + punishedPlayer.getName() + "'s turn.");

        if(!caller.removeBatteryFrom(batteryCoords.x, batteryCoords.y)) throw new IllegalArgumentException("No battery found at " + batteryCoords);

        setInputState(NONE);
        handleNextShot();
    }
    public void refuseBattery(Player caller) {
        verifyInputState(BATTERY_DECISION);
        if(caller != punishedPlayer) throw new IllegalArgumentException("It is not " + caller.getName() + "'s turn. It is currently " + punishedPlayer.getName() + "'s turn.");

        caller.removeFirstTileInPath(currentShot.getSource(), currentPathIndex);

        setInputState(NONE);
        handleNextShot();
    }

    @Override
    public String getCardType() {
        return "Pirates";
    }
}
