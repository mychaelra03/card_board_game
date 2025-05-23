package it.polimi.galaxy_trucker.server.model.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.projectile.CannonShot;
import it.polimi.galaxy_trucker.server.model.tiles.CannonTile;
import it.polimi.galaxy_trucker.server.model.tiles.EngineTile;

import java.awt.*;
import java.util.*;
import java.util.List;

import static it.polimi.galaxy_trucker.server.model.cards.CombatZoneCard.CombatRule.*;
import static it.polimi.galaxy_trucker.server.model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CombatZoneCard extends Card implements EnginesHandlerCard, CannonsHandlerCard, BatteryHandlerCard, CrewHandlerCard {
    // Compare crew, engine strength, and cannons to avoid penalties.

    public static class CombatRule {
        public enum Condition {CREW_SIZE, CANNON_POWER, ENGINE_POWER}

        public enum Punishment {FLIGHT_DAYS, CREW_LOSS, CARGO_LOSS, CANNON_SHOTS}

        private final Condition condition;
        private final Punishment punishment;
        private final int punishmentValue;
        private final CannonShot[] cannonShots;

        public CombatRule(
                @JsonProperty("condition") Condition condition,
                @JsonProperty("punishment") Punishment punishment,
                @JsonProperty("punishmentValue") int punishmentValue,
                @JsonProperty("cannonShots") CannonShot[] cannonShots) {
            this.condition = condition;
            this.punishment = punishment;
            if (punishment != Punishment.CANNON_SHOTS) {
                if (punishmentValue < 0) throw new IllegalArgumentException("punishment value must be >= 0");
                this.punishmentValue = punishmentValue;
            } else this.punishmentValue = 0;
            if (punishment == Punishment.CANNON_SHOTS) this.cannonShots = cannonShots;
            else this.cannonShots = null;
        }
        public Condition getCondition() {
            return condition;
        }
        public Punishment getPunishment() {
            return punishment;
        }
        public int getPunishmentValue() {
            return punishmentValue;
        }
        public CannonShot[] getCannonShots() {
            return cannonShots;
        }
    }

    private final CombatRule[] combatRules;

    private List<CombatRule> ruleQueue;
    private CombatRule currentRule;

    private List<Player> playerQueue;
    private Map<Player, Double> playerScores;

    private int remainingCrewToGiveUp;

    private List<CannonShot> shotQueue;
    private Player punishedPlayer;
    private CannonShot currentShot;

    @JsonCreator
    public CombatZoneCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("combatRule") CombatRule[] combatRules) {
        super(id, level);
        this.combatRules = combatRules;
    }

    @Override
    protected void onActivate() {
        this.ruleQueue = new LinkedList<>(Arrays.asList(combatRules));
        setInputState(NONE);
        applyRule();
    }

    private void applyRule() {
        verifyInputState(NONE);
        if(ruleQueue.isEmpty()) {
            setInputState(FINISHED);
            return;
        }
        this.currentRule = ruleQueue.getFirst();
        List<Player> players = board.getSortedPlayers();
        if(players.size() < 2) return;

        this.playerScores = new HashMap<>();

        switch(currentRule.getCondition()) {
            case CREW_SIZE -> {
                for(Player player : players) {
                    playerScores.put(player, (double)player.getCrewCount());
                }
                applyPunishment();
            }
            case ENGINE_POWER -> {
                this.playerQueue = board.getSortedPlayers();
                setInputState(ENGINE_SELECTION);
            }
            case CANNON_POWER -> {
                this.playerQueue = board.getSortedPlayers();
                setInputState(CANNON_SELECTION);
            }
        }
    }

    private void handleNextRule() {
        ruleQueue.removeFirst();
        applyRule();
    }

    @Override
    public void activateEngines(Player caller, int activatedEngines, List<Point> batteryTiles) {
        verifyInputState(ENGINE_SELECTION);
        verifyPlayerTurn(caller, playerQueue);
        if(activatedEngines != batteryTiles.size()) throw new IllegalArgumentException("The number of activated engines does not match the number of batteries used.");
        double strength = caller.getStrength(EngineTile.class, activatedEngines);
        caller.removeBatteries(batteryTiles);
        playerScores.put(caller, strength);
        playerQueue.removeFirst();

        if(playerQueue.isEmpty()) {
            setInputState(NONE);
            applyPunishment();
        }
    }
    @Override
    public void activateCannons(Player caller, int verticalCannons, int rotatedCannons, List<Point> batteryTiles) {
        verifyInputState(CANNON_SELECTION);
        verifyPlayerTurn(caller, playerQueue);
        if(verticalCannons + rotatedCannons != batteryTiles.size()) throw new IllegalArgumentException("The number of activated cannons does not match the number of batteries used.");
        double strength = caller.getStrength(CannonTile.class, verticalCannons, rotatedCannons);
        caller.removeBatteries(batteryTiles);
        playerScores.put(caller, strength);
        playerQueue.removeFirst();

        if(playerQueue.isEmpty()) {
            setInputState(NONE);
            applyPunishment();
        }
    }

    private void applyPunishment() {
        verifyInputState(NONE);

        Player worstPlayer = getWorstPlayer(board.getSortedPlayers(), currentRule.getCondition());

        switch(currentRule.getPunishment()) {
            case FLIGHT_DAYS -> {
                board.movePlayerBackward(worstPlayer, currentRule.getPunishmentValue());
                handleNextRule();
            }
            case CREW_LOSS -> {
                if(currentRule.getPunishmentValue() <= 0) break;
                this.punishedPlayer = worstPlayer;
                this.remainingCrewToGiveUp = currentRule.getPunishmentValue();
                setInputState(CREW_DECISION);
            }
            case CARGO_LOSS -> {
                worstPlayer.removeMostValuableCargo(currentRule.getPunishmentValue());
                handleNextRule();
            }
            case CANNON_SHOTS -> {
                this.punishedPlayer = worstPlayer;
                this.shotQueue = new LinkedList<>(Arrays.asList(currentRule.getCannonShots()));
                applyShot();
            }
        }
    }

    @Override
    public void removeMemberFrom(Player caller, Point cabinCoords) {
        verifyInputState(CREW_DECISION);
        verifyCaller(caller, punishedPlayer);
        if(remainingCrewToGiveUp <= 0) throw new RuntimeException("No more crew members are required to be removed.");
        caller.ensureCrewRemovedAt(cabinCoords);
        remainingCrewToGiveUp--;
        if(remainingCrewToGiveUp == 0 || punishedPlayer.getCrewCount() == 0) {
            setInputState(NONE);
            handleNextRule();
        }
    }

    private void applyShot() {
        verifyInputState(NONE);
        if(shotQueue.isEmpty()) {
            handleNextRule();
            return;
        }
        this.currentShot = shotQueue.getFirst();
        currentShot.randomizePathIndex();

        boolean isPlayerAtRisk = currentShot.evaluateDefense(punishedPlayer);

        if(isPlayerAtRisk) {
            setInputState(BATTERY_DECISION);
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

        caller.ensureBatteryRemovedFrom(batteryCoords);

        setInputState(NONE);
        handleNextShot();
    }
    @Override
    public void refuseBattery(Player caller) {
        verifyInputState(BATTERY_DECISION);
        verifyCaller(caller, punishedPlayer);

        caller.removeFirstTileInPath(currentShot.getSource(), currentShot.getPathIndex());

        setInputState(NONE);
        handleNextShot();
    }

    private Player getWorstPlayer(List<Player> players, Condition condition) {

        if(players.isEmpty()) throw new IllegalArgumentException("Players list cannot be empty.");

        Player worstPlayer = players.getFirst();
        if(!playerScores.containsKey(worstPlayer)) throw new RuntimeException("Player " + worstPlayer.getName() + " score was not found.");
        double worstScore = playerScores.get(worstPlayer);

        for(Player player : players) {
            if(!playerScores.containsKey(player)) throw new RuntimeException("Player " + player.getName() + " score was not found.");
            double currentScore = playerScores.get(player);

            if(currentScore < worstScore) {
                worstPlayer = player;
                worstScore = currentScore;
            }
        }
        return worstPlayer;
    }

    @Override
    public String getCardType() {
        return "Combat Zone";
    }
}
