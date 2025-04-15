package it.polimi.galaxy_trucker.Server.Model.cards.projectile;

import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.ShipBoard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static it.polimi.galaxy_trucker.Server.Model.cards.projectile.Projectile.Size.LARGE;
import static it.polimi.galaxy_trucker.Server.Model.cards.projectile.Projectile.Size.SMALL;
import static it.polimi.galaxy_trucker.ShipBoard.DefendingCannonType.DOUBLE;
import static it.polimi.galaxy_trucker.ShipBoard.DefendingCannonType.SINGLE;

public abstract class Projectile {

    private final Size size;
    private final Source source;

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

    public Size getSize() {
        return size;
    }

    public Source getSource() {
        return source;
    }

    public abstract boolean evaluateDefense(Player player, int pathIndex);

    public static int diceSumToIndex(Source source, int diceSum) {
        if(source == Projectile.Source.UP || source == Projectile.Source.DOWN) {
            return diceSum - 4;
        } else if(source == Projectile.Source.LEFT || source == Projectile.Source.RIGHT) {
            return diceSum - 5;
        }
        throw new IllegalArgumentException("Invalid source: " + source);
    }
}
