package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.cards.projectile.CannonShot;
import it.polimi.galaxy_trucker.Server.Model.cards.projectile.Projectile;
import it.polimi.galaxy_trucker.Server.Model.tiles.CannonTile;
import it.polimi.galaxy_trucker.Server.Model.tiles.EngineTile;

import java.awt.*;
import java.util.*;
import java.util.List;

import static it.polimi.galaxy_trucker.GameModel.Direction.*;
import static it.polimi.galaxy_trucker.Server.Model.cards.CombatZoneCard.CombatRule.*;
import static it.polimi.galaxy_trucker.Server.Model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CombatZoneCard extends Card {
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

    private List<CannonShot> shotQueue;
    private Player punishedPlayer;
    private CannonShot currentShot;
    private int currentPathIndex;

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

    public void activateEngines(Player caller, int activatedEngines, List<Point> batteryTiles) {
        verifyInputState(ENGINE_SELECTION);
        validatePlayerTurn(caller, playerQueue);
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
    public void activateCannons(Player caller, int verticalCannons, int rotatedCannons, List<Point> batteryTiles) {
        verifyInputState(CANNON_SELECTION);
        validatePlayerTurn(caller, playerQueue);
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

        Player worstPlayer = getWorstPlayer(board.getSortedPlayers(), currentRule.condition);

        switch(currentRule.getPunishment()) {
            case FLIGHT_DAYS -> board.movePlayer(worstPlayer, currentRule.punishmentValue, BACKWARD);
            case CREW_LOSS -> worstPlayer.removeCrew(currentRule.punishmentValue);
            case CARGO_LOSS -> worstPlayer.removeMostValuableCargo(currentRule.punishmentValue);
            case CANNON_SHOTS -> {
                this.punishedPlayer = worstPlayer;
                this.shotQueue = new LinkedList<>(Arrays.asList(currentRule.getCannonShots()));
                applyShot();
                return;
            }
        }
        handleNextRule();
    }

    private void handleNextRule() {
        ruleQueue.removeFirst();
        applyRule();
    }

    private void applyShot() {
        verifyInputState(NONE);
        if(shotQueue.isEmpty()) {
            handleNextRule();
            return;
        }
        this.currentShot = shotQueue.getFirst();
        int[] dice = GameModel.rollTwoDice();
        this.currentPathIndex = Projectile.diceSumToIndex(currentShot.getSource(), dice[0] + dice[1]);

        boolean isPlayerAtRisk = currentShot.evaluateDefense(punishedPlayer, currentPathIndex);

        if(isPlayerAtRisk) {
            setInputState(BATTERY_DECISION);
            return;
        }
        handleNextShot();
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
