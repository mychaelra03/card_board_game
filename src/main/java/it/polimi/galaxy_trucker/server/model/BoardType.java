package it.polimi.galaxy_trucker.server.model;

public enum BoardType {

    LEARNING  ("learningFlight", 18, 0, new int[] {4, 2, 1, 0}, new int[] {4, 3, 2, 1}, 2),
    LEVEL_ONE ("levelOne",       18, 2, new int[] {4, 2, 1, 0}, new int[] {4, 3, 2, 1}, 2),
    LEVEL_TWO ("levelTwo",       24, 3, new int[] {6, 3, 1, 0}, new int[] {8, 6, 4, 2}, 4);


    private final String displayName;
    private final int flightTrackLength;
    private final int buildingTimersCount;
    private final int[] startingPositions;
    private final int[] positionReward;
    private final int bestShipReward;

    BoardType(String displayName, int flightTrackLength, int buildingTimersCount, int[] startingPositions, int[] positionReward, int bestShipReward) {
        this.displayName = displayName;
        this.flightTrackLength = flightTrackLength;
        this.buildingTimersCount = buildingTimersCount;
        this.startingPositions = startingPositions;
        this.positionReward = positionReward;
        this.bestShipReward = bestShipReward;
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

    public int getPositionReward(int positionIndex) {
        if (positionIndex < 1 || positionIndex > positionReward.length) {
            throw new IllegalArgumentException("Invalid position index: " + positionIndex);
        }
        return positionReward[positionIndex - 1];
    }

    public int getBestShipReward() {
        return bestShipReward;
    }


    @Override
    public String toString() {
        return displayName;
    }
}
