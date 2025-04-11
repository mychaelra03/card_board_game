package it.polimi.galaxy_trucker.Server.Model.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.cards.projectile.CannonShot;
import it.polimi.galaxy_trucker.Server.Model.tiles.CannonTile;
import it.polimi.galaxy_trucker.Server.Model.tiles.EngineTile;

import java.util.List;

import static it.polimi.galaxy_trucker.GameModel.Direction.*;
import static it.polimi.galaxy_trucker.Server.Model.cards.CombatZoneCard.CombatRule.*;

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

    }

    //@Override
    public void applyEffects() {
        for(CombatRule rule : combatRules) {
            List<Player> players = board.getSortedPlayers();
            if(players.size() < 2) return;

            Player worstPlayer = getWorstPlayer(players, rule.condition);
            switch(rule.getPunishment()) {
                case FLIGHT_DAYS -> board.movePlayer(worstPlayer, rule.punishmentValue, BACKWARD);
                case CREW_LOSS -> worstPlayer.removeCrew(rule.punishmentValue);
                case CARGO_LOSS -> worstPlayer.removeMostValuableCargo(rule.punishmentValue); //TODO: rules does not specify if the most valuable
                case CANNON_SHOTS -> {
                    for(CannonShot shot : rule.getCannonShots()) {
                        int diceSum = GameModel.rollTwoDice();
                        worstPlayer.applyProjectile(shot, diceSum);
                    }
                }
            }
        }
    }

    private Player getWorstPlayer(List<Player> players, Condition condition) {
        Player worstPlayer = players.getFirst();
        double worstScore = getScore(worstPlayer, condition);

        for(Player player : players) {
            double currentScore = getScore(player, condition);

            if(currentScore < worstScore) {
                worstPlayer = player;
                worstScore = currentScore;
            }
        }
        return worstPlayer;
    }
    private double getScore(Player player, Condition condition) {
        if(condition == Condition.CREW_SIZE) {
            return player.getCrewCount();
        }
        if(condition == Condition.CANNON_POWER) {
            player.chooseTilesToActivate(CannonTile.class);
            return player.getCurrentStrength(CannonTile.class);
        }
        if(condition == Condition.ENGINE_POWER) {
            player.chooseTilesToActivate(EngineTile.class);
            return player.getCurrentStrength(EngineTile.class);
        }
        throw new IllegalArgumentException("Unknown condition: " + condition);
    }

    @Override
    public String getCardType() {
        return "Combat Zone";
    }
}
