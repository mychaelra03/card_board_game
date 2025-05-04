package it.polimi.galaxy_trucker.server.model.cards.projectile;

import it.polimi.galaxy_trucker.Player;

public abstract class Projectile {

    private final Size size;
    private final Source source;
    private Integer pathIndex;

    public enum Source {
        UP(0), DOWN(2), LEFT(3), RIGHT(1);
        private final int source;
        Source(int source) {
            this.source = source;
        }
        public int toInt() {
            return source;
        }
    }
    public enum Size {
        SMALL, LARGE
    }

    public Projectile(Size size, Source source) {
        this.size = size;
        this.source = source;
    }
    public Projectile(Size size, Source source, int pathIndex) {
        this(size, source);
        this.pathIndex = pathIndex;
    }

    public Size getSize() {
        return size;
    }
    public Source getSource() {
        return source;
    }

    public int getPathIndex() {
        return pathIndex;
    }

    public abstract boolean evaluateDefense(Player player);

    private static int rollDie() {
        return 1 + (int)(Math.random() * 6);
    }

    public void randomizePathIndex() {
        // If the value has been coded in (like in tests), it doesn't override it.
        if(pathIndex != null) return;
        int firstDie = rollDie();
        int secondDie = rollDie();
        int diceSum = firstDie + secondDie;

        this.pathIndex = switch (source) {
            case UP, DOWN -> diceSum - 4;
            case LEFT, RIGHT -> diceSum - 5;
        };

        // if(diceRollListener != null) diceRollListener.onDiceRolled(firstDie, secondDie);
        // TODO: dice listener
    }
}
