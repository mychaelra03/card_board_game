package it.polimi.galaxy_trucker.cards.projectile;

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
}
