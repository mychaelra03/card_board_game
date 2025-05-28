package it.polimi.galaxy_trucker.server.model.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.server.events.flight.cards.*;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.cards.projectile.CannonShot;
import it.polimi.galaxy_trucker.server.model.tiles.CannonTile;
import it.polimi.galaxy_trucker.server.model.tiles.EngineTile;

import java.awt.*;
import java.util.*;
import java.util.List;

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

        @JsonCreator
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

    public CombatZoneCard(
            @JsonProperty("id") int id,
            @JsonProperty("level") int level,
            @JsonProperty("combatRules") CombatRule[] combatRules) {
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
        List<Player> alivePlayers = board.getSortedPlayers();

        if(alivePlayers.size() < 2) {
            dispatcher.fireEvent(new CardStatusEvent(CardStatusEvent.CardStatus.COMBAT_SKIPPED));
            setInputState(FINISHED);
            return;
        }

        this.playerScores = new HashMap<>();

        switch(currentRule.getCondition()) {
            case CREW_SIZE -> {
                dispatcher.fireEvent(new CardStatusEvent(CardStatusEvent.CardStatus.RULE_CREW));
                for(Player player : alivePlayers) {
                    playerScores.put(player, (double)player.getCrewCount());
                }
                applyPunishment();
            }
            case ENGINE_POWER -> {
                dispatcher.fireEvent(new CardStatusEvent(CardStatusEvent.CardStatus.RULE_ENGINE));
                this.playerQueue = alivePlayers;

                setInputState(ENGINE_SELECTION);
                dispatcher.fireEvent(new EngineSelectionEvent(playerQueue.getFirst(), false));
            }
            case CANNON_POWER -> {
                dispatcher.fireEvent(new CardStatusEvent(CardStatusEvent.CardStatus.RULE_CANNON));
                this.playerQueue = alivePlayers;

                setInputState(CANNON_SELECTION);
                dispatcher.fireEvent(new CannonSelectionEvent(playerQueue.getFirst()));
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
        dispatcher.fireEvent(new DecisionEndEvent(caller.getName()));
        if(activatedEngines != batteryTiles.size()) throw new IllegalArgumentException("The number of activated engines does not match the number of batteries used.");
        double strength = caller.getStrength(EngineTile.class, activatedEngines);
        caller.removeBatteries(batteryTiles);
        playerScores.put(caller, strength);
        playerQueue.removeFirst();

        if (!playerQueue.isEmpty()) {
            dispatcher.fireEvent(new EngineSelectionEvent(playerQueue.getFirst(), false));
        } else {
            setInputState(NONE);
            applyPunishment();
        }
    }
    @Override
    public void activateCannons(Player caller, int verticalCannons, int rotatedCannons, List<Point> batteryTiles) {
        verifyInputState(CANNON_SELECTION);
        verifyPlayerTurn(caller, playerQueue);
        dispatcher.fireEvent(new DecisionEndEvent(caller.getName()));
        if(verticalCannons + rotatedCannons != batteryTiles.size()) throw new IllegalArgumentException("The number of activated cannons does not match the number of batteries used.");
        double strength = caller.getStrength(CannonTile.class, verticalCannons, rotatedCannons);
        caller.removeBatteries(batteryTiles);
        playerScores.put(caller, strength);
        playerQueue.removeFirst();

        if (!playerQueue.isEmpty()) {
            dispatcher.fireEvent(new CannonSelectionEvent(playerQueue.getFirst()));
        } else {
            setInputState(NONE);
            applyPunishment();
        }
    }

    private void applyPunishment() {
        verifyInputState(NONE);

        Player worstPlayer = getWorstPlayer(board.getSortedPlayers());
        dispatcher.fireEvent(new PlayerStatusEvent(worstPlayer.getName(), PlayerStatusEvent.Status.WORST_PLAYER));

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
                dispatcher.fireEvent(new CrewDecisionEvent(worstPlayer.getName(), worstPlayer.getShipContent().crewData()));
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
            dispatcher.fireEvent(new DecisionEndEvent(caller.getName()));
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

        int[] dice = currentShot.randomizePathIndex();
        dispatcher.fireEvent(new DiceThrowEvent(punishedPlayer.getName(), dice));

        boolean isPlayerAtRisk = currentShot.evaluateDefense(punishedPlayer);

        if(isPlayerAtRisk) {
            setInputState(BATTERY_DECISION);
            dispatcher.fireEvent(new BatteryDecisionEvent(punishedPlayer, currentShot));
        } else {
            handleNextShot();
        }
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

    private Player getWorstPlayer(List<Player> players) {

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
