package it.polimi.galaxy_trucker.tiles;

abstract class Tile {

    //private final int ID;
    private final Connector[] connectors = new Connector[4];

    public Tile(Connector top, Connector right, Connector bottom, Connector left) {
        this.connectors[0] = top;
        this.connectors[1] = right;
        this.connectors[2] = bottom;
        this.connectors[3] = left;
    }
    public Tile(int top, int right, int bottom, int left) {
        this(Connector.fromInt(top), Connector.fromInt(right), Connector.fromInt(bottom), Connector.fromInt(left));
    }

    public Connector getConnector(int side) {
        if(side < 0 || side > 3) throw new IllegalArgumentException("Side must be 0-3");
        return connectors[side];
    }

    //public void setConnector(int side, Connector connector) {
    //    if (side < 0 || side > 3) throw new IllegalArgumentException("Side must be 0-3");
    //    connectors[side] = connector;
    //}

    public abstract String getType();

    public void printConnectors() {
        System.out.println("Top: "+connectors[0]+", Right: "+connectors[1]+", Bottom: "+connectors[2]+", Left: "+connectors[3]);
    }
}