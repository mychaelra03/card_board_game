package it.polimi.galaxy_trucker.cards;

public class CannonShot {
    private final ProjectileSize size;
    private final ProjectileDirection direction;
    public CannonShot(ProjectileSize size, ProjectileDirection direction) {
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
