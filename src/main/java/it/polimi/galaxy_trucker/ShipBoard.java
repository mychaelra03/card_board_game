package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.tiles.*;
import it.polimi.galaxy_trucker.tiles.Tile;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ShipBoard {

    private final Tile[][] tiles = new Tile[5][7];
    private final int shipBoardType;

    public ShipBoard(int shipBoardType) {
        this.shipBoardType = shipBoardType;
        if(shipBoardType < 1 || shipBoardType > 2) throw new IllegalArgumentException("Ship Board must be type 1 or 2.");
        initializeShip();
    }

    private void initializeShip() {
        for(int i = 0; i < tiles.length; i++) {
            for(int j = 0; j < tiles[i].length; j++) {
                tiles[i][j] = new EmptyTile();
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
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        return tiles[y][x];
    }
    public void setTile(int x, int y, int tileID, int rotation) {
        // Places one of the 152 default tiles.
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(getTile(x, y).getClass() != EmptyTile.class) throw new IllegalArgumentException("Tile occupied.");
        tiles[y][x] = TileFactory.createTile(tileID);
        tiles[y][x].setRotation(rotation);
        if(!wasConnectionValid(x, y)) {
            // Should it also check if the tile is not connected to anything?
            tiles[y][x] = new EmptyTile();
            throw new IllegalArgumentException("Invalid placement, connectors don't match");
        }
        // If a cabin and an alien support are placed adjacent one another, it allows the cabin to host that type of alien.
        applyAlienSupport(x, y);
    }
    public void setTile(int x, int y, Tile tile, int rotation) {
        // Places the passed tile.
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(getTile(x, y).getClass() != EmptyTile.class) throw new IllegalArgumentException("Tile occupied.");
        tiles[y][x] = tile;
        tiles[y][x].setRotation(rotation);
        if(!wasConnectionValid(x, y)) {
            tiles[y][x] = new EmptyTile();
            throw new IllegalArgumentException("Invalid placement, connectors don't match");
        }
        applyAlienSupport(x, y);
    }

    public void removeTile(int x, int y) {
        // If AlienSupport, removes alien left without support module.
        clearAlienSupport(x, y);

        tiles[y][x] = new EmptyTile();

        // Checks if the removal of this tile left other tiles not connected to the ship.
        checkConnectivity();
    }


    // BATTERY TILES
    public int getTotalBatteriesCount() {
        int count = 0;
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tile instanceof BatteriesTile batteries) count+= batteries.getStoredBatteries();
            }
        }
        return count;
    }
    public int getBatteriesOf(int x, int y) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof BatteriesTile)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a BatteriesTile.");
        return ((BatteriesTile)tiles[y][x]).getStoredBatteries();
    }
    public boolean removeBatteryFrom(int x, int y) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof BatteriesTile)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a BatteriesTile.");
        return ((BatteriesTile)tiles[y][x]).removeBattery();
    }

    public boolean isSideShielded(int side) {
        if(getTotalBatteriesCount() == 0) return false;
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tile instanceof ShieldTile shield)
                    // A non rotated shield can protect the top and right side.
                    if(shield.getRotation() == side || shield.getRotation() == side-1) return true;
            }
        }
        return false;
    }

    // CABIN TILES
    public int getTotalHumanCount() {
        int count = 0;
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tile instanceof CabinTile cabin) count += cabin.getHumanCount();
            }
        }
        return count;
    }
    public int getTotalCrewCount() {
        int count = getTotalHumanCount();
        if(hasAlien(CrewType.BROWN_ALIEN)) count++;
        if(hasAlien(CrewType.PURPLE_ALIEN)) count++;
        return count;
    }
    public boolean hasAlien(CrewType alienType) {
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tile instanceof CabinTile cabin && cabin.hasAlien(alienType)) return true;
            }
        }
        return false;
    }

    public boolean hasAlienAt(int x, int y, CrewType alienType) {
        // For tests, maybe I'll remove it later
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof CabinTile)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a Cabin.");
        return ((CabinTile) tiles[y][x]).hasAlien(alienType);
    }
    public void setCrewTypeAt(int x, int y, CrewType crewType) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof CabinTile)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a Cabin.");
        if(crewType != CrewType.HUMAN && hasAlien(crewType)) {
            for(Tile[] row : tiles) {
                for(Tile tile : row) {
                    if(tile instanceof CabinTile cabin && cabin.hasAlien(crewType)) {
                        cabin.setCrewType(CrewType.HUMAN);
                    }
                }
            }
        }
        ((CabinTile)tiles[y][x]).setCrewType(crewType);
    }
    public CrewType removeCrewMemberAt(int x, int y, int index) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof CabinTile cabin)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a Cabin.");
        return cabin.removeMember(index);
    }

    public double getTotalCannonStrength() {
        double count = 0;
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                // Have to decide better how to set up double cannons
                if(tile instanceof CannonTile cannon) count += cannon.getStrength();
            }
        }
        if(hasAlien(CrewType.PURPLE_ALIEN) && count != 0) count += 2;
        return count;
    }

    public int getTotalEngineStrength() {
        int count = 0;
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tile instanceof EngineTile engine) count += engine.getStrength();
            }
        }
        if(hasAlien(CrewType.BROWN_ALIEN) && count != 0) count += 2;
        return count;
    }

    public int getExposedConnectorsCount() {
        int count = 0;
        int[][] offsets = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
        for(int y = 0; y < tiles.length; y++) {
            for(int x = 0; x < tiles[y].length; x++) {
                if(!(tiles[y][x] instanceof EmptyTile)) {
                    for(int i = 0; i < 4; i++) {
                        int nx = x + offsets[i][1];
                        int ny = y + offsets[i][0];
                        if(tiles[y][x].getConnector(i) != Connector.NONE) {
                            if(!isInsideShip(nx, ny)) count++;
                            else if(tiles[ny][nx] instanceof EmptyTile) count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    public BlockType loadCargoAt(int x, int y, int index, BlockType blockType) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof CargoTile cargo)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a Cargo.");
        return cargo.loadBlock(index, blockType);
    }
    public int getTotalHoldsValue() {
        int count = 0;
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tile instanceof CargoTile cargo) count += cargo.getHoldsValue();
            }
        }
        return count;
    }


    private boolean areConnectorsCompatible(Connector a, Connector b) {
        if(a == Connector.NONE && b == Connector.NONE) return true;
        if(a == Connector.NONE || b == Connector.NONE) return false;
        if(a == b) return true;
        return a == Connector.UNIVERSAL || b == Connector.UNIVERSAL;
    }

    private boolean wasConnectionValid(int x, int y) {
        // Checks the connection in all 4 directions to see if its compatible.
        Tile tile = getTile(x, y);
        int[][] offsets = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
        for(int side = 0; side < 4; side++) {
            int neighbor_x = x + offsets[side][1];
            int neighbor_y = y + offsets[side][0];
            if(!isInsideShip(neighbor_x, neighbor_y)) {
                continue;
            }
            Tile neighbor = getTile(neighbor_x, neighbor_y);
            if(neighbor instanceof EmptyTile) {
                continue;
            }
            int opposite_side = (side + 2) % 4;
            if(!areConnectorsCompatible(tile.getConnector(side), neighbor.getConnector(opposite_side))) {
                return false;
            }
        }
        return true;
    }

    public boolean isInsideShip(int x, int y) {
        // Returns true if (x,y) is inside the usable space of the spaceship.
        if(y < 0 || x < 0 || y >= tiles.length || x >= tiles[0].length) return false;
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

    private void applyAlienSupport(int x, int y) {
        // Checks if a cabin and an alien support are placed adjacent one another,
        // if so, it allows the cabin to host that type of alien.
        if(!isInsideShip(x, y)) return;

        Tile tile = getTile(x, y);
        int[][] offsets = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };

        if(tile instanceof CabinTile cabin && !(cabin instanceof StartingCabinTile)) {
            for(int i = 0; i < 4; i++) {
                if(cabin.getConnector(i) == Connector.NONE) continue;
                int nx = x + offsets[i][1];
                int ny = y + offsets[i][0];
                if(!isInsideShip(nx, ny))
                    continue;
                if(getTile(nx, ny) instanceof AlienSupportTile support) {
                    cabin.allowAlienType(support.getAlienColor(), true);
                }
            }
        }
        if(tile instanceof AlienSupportTile support) {
            for(int i = 0; i < 4; i++) {
                if(support.getConnector(i) == Connector.NONE) continue;
                int nx = x + offsets[i][1];
                int ny = y + offsets[i][0];
                if(!isInsideShip(nx, ny))
                    continue;
                if(getTile(nx, ny) instanceof CabinTile cabin && !(cabin instanceof StartingCabinTile)) {
                    cabin.allowAlienType(support.getAlienColor(), true);
                }
            }
        }
    }

    private void clearAlienSupport(int x, int y) {
        // Checks if the removed alien support was the only support for an adjacent alien inhabited cabin,
        // if so, it removes the alien.
        if(getTile(x,y) instanceof AlienSupportTile removedSupport) {
            int[][] offsets = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
            for(int i = 0; i < 4; i++) {
                if(removedSupport.getConnector(i) == Connector.NONE) continue;
                int nx = x + offsets[i][1];
                int ny = y + offsets[i][0];
                if(!isInsideShip(nx, ny)) continue;

                if(getTile(nx, ny) instanceof CabinTile cabin) {

                    if(cabin.getMemberAt(0) == removedSupport.getAlienColor()) {

                        boolean stillSupported = false;
                        for(int j = 0; j < 4; j++) {
                            if(cabin.getConnector(j) == Connector.NONE) continue;
                            int nnx = nx + offsets[j][1];
                            int nny = ny + offsets[j][0];
                            if(nnx == x && nny == y) continue; // Excludes itself
                            if(!isInsideShip(nnx, nny)) continue;
                            if(getTile(nnx, nny) instanceof AlienSupportTile support) {
                                if(support.getAlienColor() == removedSupport.getAlienColor()) {
                                    stillSupported = true;
                                    break;
                                }
                            }
                        }
                        // No need to remove cabin.allowAlienType as at this point no
                        // more aliens can be added.
                        if(!stillSupported) cabin.removeMember(0);
                    }
                }
            }
        }
    }

    private void checkConnectivity() {
        // It uses a flood fill search to divide all the tiles in groups.
        // If there are more than one groups, it automatically removes the ones without humans
        // and let the player decide which one with humans to keep.
        boolean[][] visited = new boolean[tiles.length][tiles[0].length];
        List<List<Point>> groups = new ArrayList<>();

        for(int y = 0; y < tiles.length; y++) {
            for(int x = 0; x < tiles[y].length; x++) {
                if(!visited[y][x] && !(tiles[y][x] instanceof EmptyTile)) {
                    List<Point> group = new ArrayList<>();
                    floodFill(x, y, visited, group);
                    groups.add(group);
                }
            }
        }
        if(groups.size() <= 1) return;

        List<Integer> groupsWithHumans = new ArrayList<>();
        List<Integer> groupsWithoutHumans = new ArrayList<>();
        for(int i = 0; i < groups.size(); i++) {
            if(groupHasHumans(groups.get(i))) {
                groupsWithHumans.add(i);
            } else {
                groupsWithoutHumans.add(i);
            }
        }

        // If there are both groups with crew and without humans, it removes the ones without.
        // It doesn't always delete the empty groups because if the last humans are removed, it would delete the ship.
        if(!groupsWithHumans.isEmpty()) {
            for (Integer index : groupsWithoutHumans) {
                removeGroup(groups.get(index));
            }

            if (groupsWithHumans.size() > 1) {
                int groupToKeep = chooseGroupToKeep(groupsWithHumans);
                for (int index : groupsWithHumans) {
                    if (index != groupToKeep) {
                        removeGroup(groups.get(index));
                    }
                }
            }
        }
    }
    
    private void floodFill(int x, int y, boolean[][] visited, List<Point> group) {
        // Algorithm to recursively add all connected tiles to the same group.
        if(!isInsideShip(x, y)) return;
        if(visited[y][x] || tiles[y][x] instanceof EmptyTile) return;

        visited[y][x] = true;
        group.add(new Point(x, y));
        int[][] offsets = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
        for(int i = 0; i < 4; i++) {
            if(tiles[y][x].getConnector(i) == Connector.NONE) continue;
            int nx = x + offsets[i][1];
            int ny = y + offsets[i][0];
            floodFill(nx, ny, visited, group);
        }
    }

    private boolean groupHasHumans(List<Point> group) {
        for(Point p : group) {
            if(tiles[p.y][p.x] instanceof CabinTile cabin) {
                if(cabin.getHumanCount() >= 1) return true;
            }
        }
        return false;
    }

    private void removeGroup(List<Point> group) {
        // It clears all tiles of a specific group (sets them to EmptyTile).
        for(Point p : group) {
            // I can directly set the removed tile to Empty,
            // there is no need to check alien support.
            tiles[p.y][p.x] = new EmptyTile();
        }
    }

    private int chooseGroupToKeep(List<Integer> groupsWithCrew) {
        // Lets the player choose which group of tiles to remove.
        return groupsWithCrew.getFirst();   // TEMPORARY
    }
}
