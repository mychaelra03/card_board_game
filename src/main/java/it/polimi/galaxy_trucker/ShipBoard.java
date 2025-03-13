package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.tiles.*;

public class ShipBoard {

    private final Tile[][] tiles = new Tile[7][7];
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
        // if(!IsPlacementValid()) throw new IllegalArgumentException("Invalid connectors placement.");
    }

    // private boolean IsPlacementValid() {}

    public int getExposedConnectorsCount() {
        int count = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if(tiles[i][j].getClass() != EmptyTile.class) {
                    if(tiles[i][j].getConnector(0) != Connector.NONE && tiles[i-1][j].getClass() == EmptyTile.class) count++;
                    if(tiles[i][j].getConnector(1) != Connector.NONE && tiles[i][j+1].getClass() == EmptyTile.class) count++;
                    if(tiles[i][j].getConnector(2) != Connector.NONE && tiles[i+1][j].getClass() == EmptyTile.class) count++;
                    if(tiles[i][j].getConnector(3) != Connector.NONE && tiles[i][j-1].getClass() == EmptyTile.class) count++;
                }
            }
        }
        return count;
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
