package it.polimi.galaxy_trucker.tiles;

public abstract class Tile {

    private final int ID;
    private final Connector[] connectors = new Connector[4];
    private int rotation = 0; // Clockwise

    public Tile(int ID, Connector top, Connector right, Connector bottom, Connector left) {
        this.ID = ID;
        this.connectors[0] = top;
        this.connectors[1] = right;
        this.connectors[2] = bottom;
        this.connectors[3] = left;
    }
    public Tile(int ID, int top, int right, int bottom, int left) {
        this(ID, Connector.fromInt(top), Connector.fromInt(right), Connector.fromInt(bottom), Connector.fromInt(left));
    }
    public Tile() {
        this.ID = 0;
    }

    public int getID() {
        return ID;
    }

    public int getRotation() {
        return rotation;
    }

    // The rotation logic is done in JavaFX
    public void setRotation(int rotation) {
        if(rotation < 0 || rotation > 3) throw new IllegalArgumentException("Rotation must be 0-3");
        this.rotation = rotation;
    }

    public Connector[] getConnectors() {
        return connectors;
    }

    public Connector getConnector(int side) {
        if(side < 0 || side > 3) throw new IllegalArgumentException("Side must be 0-3");
        return connectors[(side - rotation + 4) % 4];
    }

    public abstract String getTileType();

    // For debugging
    public void printConnectors() {
        System.out.println("Top: "+getConnector(0)+", Right: "+getConnector(1)+", Bottom: "+getConnector(2)+", Left: "+getConnector(3));
    }
}