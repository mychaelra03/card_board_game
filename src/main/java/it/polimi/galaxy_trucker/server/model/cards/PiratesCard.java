package it.polimi.galaxy_trucker.server.model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.server.events.flight.cards.BatteryDecisionEvent;
import it.polimi.galaxy_trucker.server.events.flight.cards.DecisionEndEvent;
import it.polimi.galaxy_trucker.server.events.flight.cards.DiceThrowEvent;
import it.polimi.galaxy_trucker.server.events.flight.cards.ProjectileEvent;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.projectile.CannonShot;

import java.awt.*;
import java.util.*;
import java.util.List;

import static it.polimi.galaxy_trucker.server.model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class PiratesCard extends AbstractEnemy implements BatteryHandlerCard {
    // Combat encounter, win to gain credits, lose to get shot.

    private final int rewardCredits;
    private final CannonShot[] cannonShots;

    private List<CannonShot> shotQueue;
    private Player punishedPlayer;
    private CannonShot currentShot;

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

        int[] dice = currentShot.randomizePathIndex();
        dispatcher.fireEvent(new DiceThrowEvent(punishedPlayer.getName(), dice));

        boolean isPlayerAtRisk = currentShot.evaluateDefense(punishedPlayer);

        if(isPlayerAtRisk) {
            setInputState(BATTERY_DECISION);
            dispatcher.fireEvent(new BatteryDecisionEvent(punishedPlayer, currentShot));
        }
        else handleNextShot();
    }

    private void handleNextShot() {
        shotQueue.removeFirst();
        applyShot();
    }

    @Override
    public void useBattery(Player caller, Point batteryCoords) {
        verifyInputState(BATTERY_DECISION);
        verifyCaller(caller, punishedPlayer);

        dispatcher.fireEvent(new DecisionEndEvent(caller.getName()));

        caller.ensureBatteryRemovedFrom(batteryCoords);

        dispatcher.fireEvent(new ProjectileEvent(caller, currentShot, null, true));

        setInputState(NONE);
        handleNextShot();
    }
    @Override
    public void refuseBattery(Player caller) {
        verifyInputState(BATTERY_DECISION);
        verifyCaller(caller, punishedPlayer);

        dispatcher.fireEvent(new DecisionEndEvent(caller.getName()));

        caller.applyProjectile(currentShot);

        setInputState(NONE);
        handleNextShot();
    }

    @Override
    public String getCardType() {
        return "Pirates";
    }
}
