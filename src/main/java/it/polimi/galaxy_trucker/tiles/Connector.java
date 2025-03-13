package it.polimi.galaxy_trucker.tiles;

public enum Connector {
    NONE(0), SINGLE(1), DOUBLE(2), UNIVERSAL(3);

    private final int value;

    Connector(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Connector fromInt(int num) {
        for (Connector c : values()) {
            if (c.value == num) {
                return c;
            }
        }
        throw new IllegalArgumentException("Invalid connector value: " + num);
    }
}