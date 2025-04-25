package it.polimi.galaxy_trucker.Server.Model.tiles;

public abstract class Tile {

    private final int id;
    private final Connector[] connectors;
    private int rotation; // Clockwise

    public Tile(int id, Connector[] connectors) {
        this.id = id;
        this.connectors = connectors;
        this.rotation = 0;
    }

    public int getId() {
        return id;
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
}