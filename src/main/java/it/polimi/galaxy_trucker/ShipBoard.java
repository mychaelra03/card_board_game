package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.tiles.*;

public class ShipBoard {

    private final Tile[][] tiles = new Tile[5][7];
    private final int shipBoardType;

    public ShipBoard(int shipBoardType) {
        this.shipBoardType = shipBoardType;
        if(shipBoardType < 1 || shipBoardType > 2) throw new IllegalArgumentException("Ship Board must be type 1 or 2.");
        initializeShip();
    }

    private void initializeShip() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                tiles[i][j] = new EmptyTile(); // Assuming Tile has a default constructor
            }
        }
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public int getShipBoardType() {
        return shipBoardType;
    }

    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }

    public void setTile(int x, int y, Tile tile, int rotation) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates.");
        if(tiles[x][y].getClass() != EmptyTile.class) throw new IllegalArgumentException("Tile occupied.");
        tiles[x][y] = tile;
        tiles[x][y].setRotation(rotation);
        // Connectors placement checks...
        if(!wasConnectionValid(x, y)) throw new IllegalArgumentException("Invalid connectors placement.");
    }

    public int getBatteriesCount() {
        int count = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(tiles[i][j] instanceof BatteriesTile) count += ((BatteriesTile) tiles[i][j]).getStoredBatteries();
            }
        }
        return count;
    }

    public int getHumanCount() {
        int count = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(tiles[i][j] instanceof CabinTile) count += ((CabinTile) tiles[i][j]).getHumanCount();
            }
        }
        return count;
    }
    public boolean hasBrownAlien() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(tiles[i][j] instanceof CabinTile && ((CabinTile)tiles[i][j]).hasBrownAlien()) return true;
            }
        }
        return false;
    }
    public boolean hasPurpleAlien() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(tiles[i][j] instanceof CabinTile && ((CabinTile)tiles[i][j]).hasPurpleAlien()) return true;
            }
        }
        return false;
    }

    public double getTotalCannonStrength() {
        double count = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(tiles[i][j] instanceof CannonTile) count += ((CannonTile) tiles[i][j]).getStrength();
            }
        }
        if(hasPurpleAlien() && count != 0) count += 2;
        return count;
    }

    public int getTotalEngineStrength() {
        int count = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(tiles[i][j] instanceof EngineTile) count += ((EngineTile) tiles[i][j]).getStrength();
            }
        }
        if(hasBrownAlien() && count != 0) count += 2;
        return count;
    }

    public int getExposedConnectorsCount() {
        int count = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(!(tiles[i][j] instanceof EmptyTile)) {
                    if(tiles[i][j].getConnector(0) != Connector.NONE && tiles[i-1][j] instanceof EmptyTile) count++;
                    if(tiles[i][j].getConnector(1) != Connector.NONE && tiles[i][j+1] instanceof EmptyTile) count++;
                    if(tiles[i][j].getConnector(2) != Connector.NONE && tiles[i+1][j] instanceof EmptyTile) count++;
                    if(tiles[i][j].getConnector(3) != Connector.NONE && tiles[i][j-1] instanceof EmptyTile) count++;
                } // Could be written in a cleaner way
            }
        }
        return count;
    }

    public int calculateCargoValue() {
        int count = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(tiles[i][j] instanceof CargoTile) count += ((CargoTile)tiles[i][j]).getHoldsValue();
            }
        }
        return count;
    }

    private boolean areConnectorsCompatible(Connector a, Connector b) {
        if (a == Connector.NONE && b == Connector.NONE) return true;
        if (a == Connector.NONE || b == Connector.NONE) return false;
        if (a == b) return true;
        if (a == Connector.UNIVERSAL || b == Connector.UNIVERSAL) return true;
        return false;
    }

    private boolean wasConnectionValid(int x, int y) {
        Tile tile = getTile(x, y);

        int[][] offsets = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };

        for (int side = 0; side < 4; side++) {
            int nx = x + offsets[side][0];
            int ny = y + offsets[side][1];

            if (!isInsideShip(nx, ny)) {
                continue;
            }

            Tile neighbor = getTile(nx, ny);
            if (neighbor instanceof EmptyTile) {
                continue;
            }

            Connector connectorHere = tile.getConnector(side);
            Connector connectorNeighbor = neighbor.getConnector((side + 2) % 4);

            if (!areConnectorsCompatible(connectorHere, connectorNeighbor)) {
                return false;
            }
        }
        return true;
    }


    public boolean isInsideShip(int x, int y) {
        if(shipBoardType == 1) {
            int[][] type1 = {
                    {0, 0, 0, 1, 0, 0, 0},
                    {0, 0, 1, 1, 1, 0, 0},
                    {0, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 0, 1, 1, 0}
            };
            return type1[y][x] == 1;
        }
        else {
            int[][] type2 = {
                    {0, 0, 1, 0, 1, 0, 0},
                    {0, 1, 1, 1, 1, 1, 0},
                    {1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 0, 1, 1, 1}
            };
            return type2[y][x] == 1;
        }
    }
}
