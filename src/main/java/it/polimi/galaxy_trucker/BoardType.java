package it.polimi.galaxy_trucker;

public enum BoardType {
    LEARNING, LEVEL_TWO;

    @Override
    public String toString() {
        switch (this) {
            case LEARNING:
                return "learningFlight";
            case LEVEL_TWO:
                return "levelTwo";
            default:
                throw new IllegalArgumentException("Unexpected value: " + this);
        }
    }
}
