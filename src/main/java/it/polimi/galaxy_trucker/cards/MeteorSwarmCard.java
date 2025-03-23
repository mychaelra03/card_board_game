package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.GameBoard;

public class MeteorSwarmCard extends Card {
    // Rolling dice determines if meteors hit your ship.

    // TODO: check if Meteor should be an inner class or not (it's utilized only in here)
    public static class Meteor {
        private final ProjectileSize size;
        private final ProjectileDirection direction;
        public Meteor(ProjectileSize size, ProjectileDirection direction) {
            this.size = size;
            this.direction = direction;
        }
        public ProjectileSize getSize() {
            return size;
        }
        public ProjectileDirection getDirection() {
            return direction;
        }
    }
    private final Meteor[] meteors;

    public MeteorSwarmCard(int ID, int level, Meteor[] meteors) {
        super(ID, level);
        this.meteors = meteors;
    }

    @Override
    public void applyEffects(GameBoard board) {

    }

    @Override
    public String getCardType() {
        return "Meteor Swarm";
    }
}
