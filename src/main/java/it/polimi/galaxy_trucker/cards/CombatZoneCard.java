package it.polimi.galaxy_trucker.cards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameBoard;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CombatZoneCard extends Card {
    // Compare crew, engine strength, and cannons to avoid penalties.

    public static class CombatRule {
        public enum Condition { CREW_SIZE, CANNON_POWER, ENGINE_POWER }
        public enum Punishment { FLIGHT_DAYS, CARGO_LOSS, CREW_LOSS, CANNON_SHOTS }
        private final Condition condition;
        private final Punishment punishment;
        private final int punishmentValue;
        private final CannonShot[] cannonShots;



        @JsonCreator
        public CombatRule(@JsonProperty("condition") Condition condition, @JsonProperty("punishment")Punishment punishment,@JsonProperty("punishmentValue") int punishmentValue,@JsonProperty("cannonShots") CannonShot[] cannonShots) {
            this.condition = condition;
            this.punishment = punishment;
            if(punishment != Punishment.CANNON_SHOTS) {
                if(punishmentValue < 0) throw new IllegalArgumentException("punishment value must be >= 0");
                this.punishmentValue = punishmentValue;
            }
            else this.punishmentValue = 0;
            if (punishment==Punishment.CANNON_SHOTS) this.cannonShots = cannonShots;
            else this.cannonShots = null;


        }

        public Condition getCondition() { return condition; }
        public Punishment getPunishment() { return punishment; }
        public int getPunishmentValue() { return punishmentValue; }
        public CannonShot[] getCannonShots() { return cannonShots; }


    }

    private final CombatRule[] combatRules;

    @JsonCreator
    public CombatZoneCard(@JsonProperty("id")int id, @JsonProperty("level")int level, @JsonProperty("combatRule")CombatRule[] combatRules) {
        super(id, level);
        this.combatRules = combatRules;
    }

    @Override
    public void applyEffects(GameBoard board) {

    }

    @Override
    public String getCardType() {
        return "Combat Zone";
    }


}
