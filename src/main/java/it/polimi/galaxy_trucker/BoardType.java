package it.polimi.galaxy_trucker;

public enum BoardType {
    LEARNING, LEVEL_ONE, LEVEL_TWO;

    public int getFlightTrackLength() {
        return switch (this) {
            case LEARNING, LEVEL_ONE -> 18;
            case LEVEL_TWO -> 24;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case LEARNING -> "learningFlight";
            case LEVEL_ONE -> "levelOne";
            case LEVEL_TWO -> "levelTwo";
        };
    }
}
