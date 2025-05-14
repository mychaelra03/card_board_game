package it.polimi.galaxy_trucker.server.model;

public enum BoardType {

    LEARNING  ("learningFlight", 18, 0, new int[] {4, 2, 1, 0}),
    LEVEL_ONE ("levelOne",       18, 2, new int[] {4, 2, 1, 0}),
    LEVEL_TWO ("levelTwo",       24, 3, new int[] {6, 3, 1, 0});


    private final String displayName;
    private final int flightTrackLength;
    private final int buildingTimersCount;
    private final int[] startingPositions;

    BoardType(String displayName, int flightTrackLength, int buildingTimersCount, int[] startingPositions) {
        this.displayName = displayName;
        this.flightTrackLength = flightTrackLength;
        this.buildingTimersCount = buildingTimersCount;
        this.startingPositions = startingPositions;
    }

    public int getFlightTrackLength() {
        return flightTrackLength;
    }

    public int getBuildingTimersCount() {
        return buildingTimersCount;
    }

    public int getStartingPosition(int positionIndex) {
        if (positionIndex < 1 || positionIndex > startingPositions.length) {
            throw new IllegalArgumentException("Invalid position index: " + positionIndex);
        }
        return startingPositions[positionIndex - 1];
    }

    @Override
    public String toString() {
        return displayName;
    }
}
