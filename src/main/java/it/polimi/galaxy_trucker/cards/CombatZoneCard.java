package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.GameBoard;

public class CombatZoneCard extends Card {
    // Compare crew, engine strength, and cannons to avoid penalties.

    public static class CombatRule {
        public enum Condition { CREW_SIZE, CANNON_POWER, ENGINE_POWER }
        public enum Punishment { FLIGHT_DAYS, CARGO_LOSS, CREW_LOSS, CANNON_SHOTS }
        private final Condition condition;
        private final Punishment punishment;
        private final int punishmentValue;
        private final CannonShot[] cannonShots;
        public CombatRule(Condition condition, Punishment punishment, int punishmentValue) {
            this.condition = condition;
            if(punishment == Punishment.CANNON_SHOTS) throw new IllegalArgumentException("Wrong use of the constructor");
            this.punishment = punishment;
            this.punishmentValue = punishmentValue;
            this.cannonShots = null;
        }
        public CombatRule(Condition condition, Punishment punishment, CannonShot[] cannonShots) {
            this.condition = condition;
            if(punishment != Punishment.CANNON_SHOTS) throw new IllegalArgumentException("Wrong use of the constructor");
            this.punishment = punishment;
            this.punishmentValue = 0;
            this.cannonShots = cannonShots;
        }
        public Condition getCondition() { return condition; }
        public Punishment getPunishment() { return punishment; }
        public int getPunishmentValue() { return punishmentValue; }
        public CannonShot[] getCannonShots() { return cannonShots; }

    }

    private final CombatRule[] combatRules;

    public CombatZoneCard(int ID, int level, CombatRule[] combatRules) {
        super(ID, level);
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
