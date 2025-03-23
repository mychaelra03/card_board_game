package it.polimi.galaxy_trucker;

public class Player {

    public enum Color{
        BLUE, RED, YELLOW, GREEN
    }
    private String name;
    private Color color;
    private ShipBoard shipBoard;
    private int credits;
    private int position;
    private boolean isEliminated;

    public Player(String name, Color color, int shipType) {
        this.name = name;
        this.color = color;
        this.shipBoard = new ShipBoard(shipType, color);
        this.credits = 0;
        this.position = 0;
        this.isEliminated = false;
    }

    public String getName() {
        return name;
    }
    public Color getColor() {
        return color;
    }
    public ShipBoard getShipBoard() {
        return shipBoard;
    }
    public int getCredits() {
        return credits;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public int getPosition() {
        return position;
    }
    public boolean isEliminated() {
        return isEliminated;
    }

    // TODO: public void getShot(CannonShot[] cannonShots);
}
