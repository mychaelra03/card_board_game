package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.tiles.*;
import it.polimi.galaxy_trucker.tiles.Tile;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShipBoard {

    private final Tile[][] tiles = new Tile[5][7];
    private final int shipBoardType;
    private final Player.Color playerColor;

    // TODO: Cannon and Engine checks.
    // TODO: IsColumnCannonProtected.
    // TODO: All checks at the end of the building phase.

    // TODO: isDirectionCannonProtected()


    public ShipBoard(int shipBoardType, Player.Color playerColor) {
        this.shipBoardType = shipBoardType;
        if(shipBoardType < 1 || shipBoardType > 2) throw new IllegalArgumentException("Ship Board must be type 1 or 2.");
        initializeShip();
        this.playerColor = playerColor;
    }

    private void initializeShip() {
        // Empties the ship and places the starting cabin.
        for(int i = 0; i < tiles.length; i++) {
            for(int j = 0; j < tiles[i].length; j++) {
                tiles[i][j] = new EmptyTile();
            }
        }
        tiles[2][3] = new StartingCabinTile(playerColor);
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public int getShipBoardType() {
        return shipBoardType;
    }

    public Tile getTile(int x, int y) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+") is outside the ship board.");
        return tiles[y][x];
    }
    public void setTile(int x, int y, int tileID, int rotation) {
        // Places one of the 152 default tiles.
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+") is outside the ship board.");
        if(getTile(x, y).getClass() != EmptyTile.class) throw new IllegalArgumentException("Tile occupied.");
        tiles[y][x] = TileFactory.getInstance().getTile(tileID);
        //tiles[y][x] = TileFactory.createTile(tileID);
        tiles[y][x].setRotation(rotation);
        //if(!areTileConnectionsValid(x, y)) {
        //    // Checks if the tile is not connected or if the connectors are not compatible.
        //    //
        //    //tiles[y][x] = new EmptyTile();
        //    throw new IllegalArgumentException("Invalid placement, connectors don't match");
        //}
        // If a cabin and an alien support are placed adjacent one another, it allows the cabin to host that type of alien.
        //applyAlienSupport(x, y);
    }
    public void setTile(int x, int y, Tile tile, int rotation) {
        // Places the passed tile (used for testing).
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+") is outside the ship board.");
        if(tiles[y][x].getClass() != EmptyTile.class) throw new IllegalArgumentException("Tile occupied.");
        tiles[y][x] = tile;
        tiles[y][x].setRotation(rotation);
//        if(!areTileConnectionsValid(x, y)) {
//            tiles[y][x] = new EmptyTile();
//            throw new IllegalArgumentException("Invalid placement, connectors don't match");
//        }
//        applyAlienSupport(x, y);
        updateAlienSupport();
    }

    public void removeTile(int x, int y) {

        tiles[y][x] = new EmptyTile();

        updateAlienSupport();
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

    // SHIELD TILES
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
    public CrewType removeCrewMember(int x, int y, int index) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof CabinTile cabin)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a Cabin.");
        return cabin.removeMember(index);
    }
    public void applyEpidemicCard() {
        Set<CabinTile> infectedCabins = new HashSet<>();

        for(int y = 0; y < tiles.length; y++) {
            for(int x = 0; x < tiles[0].length; x++) {
                if(!isInsideShip(x, y)) continue;

                if(tiles[y][x] instanceof CabinTile cabin && cabin.hasCrew()) {
                    int[][] offsets = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };

                    for(int i = 0; i < 4; i++) {
                        if(cabin.getConnector(i) == Connector.NONE) continue;
                        int nx = x + offsets[i][1];
                        int ny = y + offsets[i][0];
                        if(!isInsideShip(nx, ny)) continue;

                        if(tiles[ny][nx] instanceof CabinTile neighbour && neighbour.hasCrew()) infectedCabins.add(cabin);
                    }
                }
            }
        }
        for (CabinTile infected : infectedCabins) {
            infected.removeOneMember();
        }
    }

    // CANNON TILES
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
    public double getMinCannonStrength() {
        double count = 0;
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tile instanceof CannonTile cannon && !cannon.isDouble()) count += cannon.getStrength();
            }
        }
        if(hasAlien(CrewType.PURPLE_ALIEN) && count != 0) count += 2;
        return count;
    }
    public double getMaxCannonStrength() {
        double count = 0;
        int batteries = getTotalBatteriesCount();
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tile instanceof CannonTile cannon) {
                    if(cannon.isDouble()) {
                        if(cannon.getRotation() == 0 && batteries > 0) {
                            count += cannon.getStrength();
                            batteries--;
                        }
                    } else {
                        count += cannon.getStrength();
                    }
                }
            }
        }
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(batteries == 0) break;
                if(tile instanceof CannonTile cannon && cannon.isDouble()) {
                    if(cannon.getRotation() != 0 && batteries > 0) {
                        count += cannon.getStrength();
                        batteries--;
                    }
                }
            }
        }
        if(hasAlien(CrewType.PURPLE_ALIEN) && count != 0) count += 2;
        return count;
    }

    // ENGINE TILES
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

    // CARGO TILES
    public BlockType loadCargoAt(int x, int y, int index, BlockType blockType) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof CargoTile cargo)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a Cargo.");
        return cargo.loadBlock(index, blockType);
    }
    public BlockType removeCargoAt(int x, int y, int index) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof CargoTile cargo)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a Cargo.");
        return cargo.removeBlock(index);
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

    private boolean hasValidConnections(int x, int y) {
        // Checks the connection in all 4 directions to see if its compatible.
        int connectedSides = 0;
        Tile tile = getTile(x, y);
        int[][] offsets = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
        for(int side = 0; side < 4; side++) {
            int neighbor_x = x + offsets[side][1];
            int neighbor_y = y + offsets[side][0];

            if(!isInsideShip(neighbor_x, neighbor_y)) continue;

            Tile neighbor = getTile(neighbor_x, neighbor_y);
            if(neighbor instanceof EmptyTile) continue;

            int opposite_side = (side + 2) % 4;
            if(!areConnectorsCompatible(tile.getConnector(side), neighbor.getConnector(opposite_side))) return false;

            if(tile.getConnector(side) != Connector.NONE) connectedSides++;
        }
        // Uses connectedSides to count how many sides are connected to other tiles.
        return connectedSides != 0;
    }

    public boolean isInsideShip(int x, int y) {
        // Returns true if(x,y) is inside the usable space of the spaceship.
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

    private void updateAlienSupport() {
        // Checks if a cabin and an alien support are placed adjacent one another,
        // if so, it allows the cabin to host that type of alien.
        for(int y = 0; y < tiles.length; y++) {
            for(int x = 0; x < tiles[0].length; x++) {

                if(!isInsideShip(x, y)) continue;
                if(tiles[y][x] instanceof CabinTile cabin && !(cabin instanceof StartingCabinTile)) {
                    boolean canHostBrown = false;
                    boolean canHostPurple = false;
                    int[][] offsets = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

                    for (int i = 0; i < 4; i++) {
                        if (cabin.getConnector(i) == Connector.NONE) continue;
                        int nx = x + offsets[i][1];
                        int ny = y + offsets[i][0];
                        if (!isInsideShip(nx, ny)) continue;

                        if (tiles[ny][nx] instanceof AlienSupportTile support) {
                            if(support.getAlienColor() == CrewType.BROWN_ALIEN) canHostBrown = true;
                            if(support.getAlienColor() == CrewType.PURPLE_ALIEN) canHostPurple = true;
                        }
                    }
                    cabin.allowAlienType(CrewType.BROWN_ALIEN, canHostBrown);
                    cabin.allowAlienType(CrewType.PURPLE_ALIEN, canHostPurple);
                }
            }
        }
    }

    public HashSet<Point> checkShipLayout() {
        // FIXME
        // Adds all invalid tile's coordinates to invalidTiles
        HashSet<Point> invalidTiles = new HashSet<>();

        for(int y = 0; y < tiles.length; y++) {
            for(int x = 0; x < tiles[0].length; x++) {
                if(!isInsideShip(x, y)) continue;
                Tile tile = getTile(x, y);
                if(tile instanceof EmptyTile) continue;
                if(tile instanceof EngineTile engine) {
                    if(engine.getRotation() != 0) {
                        invalidTiles.add(new Point(x, y));
                        continue;
                    }
                }
                if(tile instanceof EngineTile || tile instanceof CannonTile) {
                    int[][] offsets = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
                    int nx = x + offsets[tile.getRotation()][1];
                    int ny = y + offsets[tile.getRotation()][0];
                    if(isInsideShip(nx, ny) && !(tiles[ny][nx] instanceof EmptyTile)) {
                        invalidTiles.add(new Point(x, y));
                        invalidTiles.add(new Point(nx, ny));
                        continue;
                    }
                }
                if(!hasValidConnections(x,y)) {
                    invalidTiles.add(new Point(x, y));
                    continue;
                }
                // TODO: should I apply alien support here or in a different method?
                // TODO: check tile before applyAlienSupport
            }
        }
        checkConnectivity();
        updateAlienSupport();
        return invalidTiles;
    }

    private boolean isConnectedToType(int x, int y, Class<? extends Tile> type) {
        if(!isInsideShip(x, y)) return false;

        Tile tile = getTile(x, y);
        int[][] offsets = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };

        for(int i = 0; i < 4; i++) {
            if(tile.getConnector(i) == Connector.NONE) continue;
            int nx = x + offsets[i][1];
            int ny = y + offsets[i][0];
            if(!isInsideShip(nx, ny)) continue;

            if(type.isInstance(getTile(nx, ny))) return true;
        }
        return false;
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
            for(Integer index : groupsWithoutHumans) {
                removeGroup(groups.get(index));
            }

            if(groupsWithHumans.size() > 1) {
                int groupToKeep = chooseGroupToKeep(groupsWithHumans);
                for(int index : groupsWithHumans) {
                    if(index != groupToKeep) {
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
