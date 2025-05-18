package it.polimi.galaxy_trucker.server.model.tiles;

public enum CrewType {
    NONE,
    SINGLE_HUMAN,
    DOUBLE_HUMAN,
    BROWN_ALIEN,
    PURPLE_ALIEN;

    public boolean isHuman() {
        return this == SINGLE_HUMAN || this == DOUBLE_HUMAN;
    }

    public boolean isAlien() {
        return this == BROWN_ALIEN || this == PURPLE_ALIEN;
    }

    public int humanCount() {
        return switch (this) {
            case DOUBLE_HUMAN -> 2;
            case SINGLE_HUMAN -> 1;
            default -> 0;
        };
    }
}