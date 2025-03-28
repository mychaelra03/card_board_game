package it.polimi.galaxy_trucker.Model.tiles;

public enum BlockType {
    RED(4), YELLOW(3), GREEN(2), BLUE(1);

    private final int value;

    BlockType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}