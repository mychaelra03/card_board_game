package it.polimi.galaxy_trucker.server.model;

import it.polimi.galaxy_trucker.server.model.cards.projectile.Projectile;
import it.polimi.galaxy_trucker.server.model.tiles.*;

import java.awt.Point;
import java.util.*;
import java.util.function.Predicate;

import static it.polimi.galaxy_trucker.server.model.tiles.CrewType.*;

public class ShipBoard {

    private final Tile[][] tiles;
    private final BoardType shipType;
    private int removedTiles;

    public ShipBoard(BoardType shipType) {
        this.tiles = new Tile[5][7];
        this.shipType = shipType;
        initializeShip();
        removedTiles = 0;
    }

    private void initializeShip() {
        // Empties the ship and places the starting cabin.
        for(int i = 0; i < tiles.length; i++) {
            for(int j = 0; j < tiles[i].length; j++) {
                tiles[i][j] = new EmptyTile();
            }
        }

        tiles[2][3] = new StartingCabinTile();
    }

    public Tile[][] getTiles() {
        return tiles;
    }
    public int getRemovedTilesCount() {
        return removedTiles;
    }
    public void addToRemovedTiles(int amount) {
        removedTiles += amount;
    }

    public BoardType getShipType() {
        return shipType;
    }

    public Tile getTile(int x, int y) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+") is outside the ship board.");
        return tiles[y][x];
    }

    public void setTile(int x, int y, int tileId, int rotation) {
        // Places one of the 152 default tiles.
        Tile tile = TileFactory.getInstance().getTile(tileId);
        placeTile(x, y, tile, rotation);
    }
    public void setTile(int x, int y, Tile tile, int rotation) {
        // Places the passed tile (used for testing).
        placeTile(x, y, tile, rotation);
    }
    private void placeTile(int x, int y, Tile tile, int rotation) {
        if(tile instanceof EmptyTile || tile instanceof StartingCabinTile) throw new IllegalArgumentException("Cannot manually place tile type: " + tile.getTileType());
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+") is outside the ship board.");
        if(tiles[y][x].getClass() != EmptyTile.class) throw new IllegalArgumentException("Tile occupied.");
        tiles[y][x] = tile;
        tiles[y][x].setRotation(rotation);
        if(tile instanceof CabinTile || tile instanceof AlienSupportTile) updateAlienSupport();
    }

    public void removeTile(int x, int y) {
        tiles[y][x] = new EmptyTile();

        updateAlienSupport();
        // Checks if the removal of this tile left other tiles not connected to the ship.
        removedTiles++;
        updateConnectivity();
    }

    // BATTERY TILES
    public int getTotalBatteriesCount() {
        int count = 0;
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tile instanceof BatteriesTile batteries) count += batteries.getStoredBatteries();
            }
        }
        return count;
    }
    public int getBatteriesOf(int x, int y) {
        // for debugging.
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof BatteriesTile)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a BatteriesTile.");
        return ((BatteriesTile)tiles[y][x]).getStoredBatteries();
    }
    public boolean removeBatteryFrom(int x, int y) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof BatteriesTile)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a BatteriesTile.");
        return ((BatteriesTile)tiles[y][x]).removeBattery();
    }
    public void removeBatteries(List<Point> coordinates) {
        for(Point tile : coordinates) {
            if(!removeBatteryFrom(tile.x, tile.y)) throw new IllegalArgumentException("No battery found at " + tile);
        }
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
        if(hasAlien(BROWN_ALIEN)) count++;
        if(hasAlien(PURPLE_ALIEN)) count++;
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
        // for debugging
        // For tests, maybe I'll remove it later
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof CabinTile)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a Cabin.");
        return ((CabinTile) tiles[y][x]).hasAlien(alienType);
    }
    public void setCrewTypeAt(int x, int y, CrewType crewType) {
        // used in tests for easy crew setup
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof CabinTile)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a Cabin.");
        if(crewType.isAlien() && hasAlien(crewType)) {
            for(Tile[] row : tiles) {
                for(Tile tile : row) {
                    if(tile instanceof CabinTile cabin && cabin.hasAlien(crewType)) {
                        cabin.setCrewType(DOUBLE_HUMAN);
                    }
                }
            }
        }
        ((CabinTile)tiles[y][x]).setCrewType(crewType);
    }
    public void cycleCrewTypeAt(int x, int y) {
        if (!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: (" + x + "," + y + ").");
        if (!(tiles[y][x] instanceof CabinTile cabin)) throw new IllegalArgumentException("Tile at (" + x + "," + y + ") is not a Cabin.");

        CrewType current = cabin.getCrew();
        List<CrewType> cycleOptions = cabin.getValidCrewTypesForCycle();

        // Cycle to the next one
        int nextIndex = (cycleOptions.indexOf(current) + 1) % cycleOptions.size();
        CrewType next = cycleOptions.get(nextIndex);

        // Enforce alien uniqueness if applicable
        if (next.isAlien() && hasAlien(next)) {
            // Remove existing alien of that type
            for (Tile[] row : tiles) {
                for (Tile t : row) {
                    if (t instanceof CabinTile otherCabin && otherCabin.hasAlien(next)) {
                        otherCabin.setCrewType(DOUBLE_HUMAN);
                    }
                }
            }
        }
        cabin.setCrewType(next);
    }
    public CrewType removeCrewMemberAt(int x, int y) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof CabinTile cabin)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a Cabin.");
        return cabin.removeMember();
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
        for(CabinTile infected : infectedCabins) {
            infected.removeMember();
        }
    }

    // ACTIVATABLE TILES
    public double getStrength(Class<? extends ActivatableTile> tileClass, int doubleVertical, int doubleRotated) {
        return StrengthCalculator.calculateStrength(this, tileClass, doubleVertical, doubleRotated);
    }
    public double getStrength(Class<? extends ActivatableTile> tileClass, int doubleVertical) {
        return StrengthCalculator.calculateStrength(this, tileClass, doubleVertical, 0);
    }

    public double getMinStrength(Class<? extends ActivatableTile> tileClass) {
        return getStrength(tileClass, 0, 0);
    }
    public double getMaxStrength(Class<? extends ActivatableTile> tileClass) {
        int batteries = getTotalBatteriesCount();

        int doubleVerticalAvailable = getTileCount(tileClass, tile -> ((ActivatableTile) tile).isDouble() && tile.getRotation() == 0);
        int doubleVertical = Math.min(batteries, doubleVerticalAvailable);
        batteries -= doubleVertical;

        int doubleRotatedAvailable = getTileCount(tileClass, tile -> ((ActivatableTile) tile).isDouble() && tile.getRotation() != 0);
        int doubleRotated = Math.min(batteries, doubleRotatedAvailable);

        return getStrength(tileClass, doubleVertical, doubleRotated);
    }


    public int getExposedConnectorsCount() {
        int count = 0;
        int[][] offsets = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
        for(int y = 0; y < tiles.length; y++) {
            for(int x = 0; x < tiles[0].length; x++) {
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
    public BlockType unloadCargoAt(int x, int y, int index) {
        if(!isInsideShip(x, y)) throw new IllegalArgumentException("Invalid coordinates: ("+x+","+y+").");
        if(!(tiles[y][x] instanceof CargoTile cargo)) throw new IllegalArgumentException("Tile at ("+x+","+y+") is not a Cargo.");
        return cargo.unloadBlock(index);
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
    public void removeMostValuableCargo(int quantity) {
        class BlockInfo {
            final CargoTile tile;
            final int index;
            final int value;
            BlockInfo(CargoTile tile, int index, int value) {
                this.tile = tile;
                this.index = index;
                this.value = value;
            }
        }
        List<BlockInfo> cargoBlocks = new ArrayList<>();
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tile instanceof CargoTile cargo) {
                    for(int index = 0; index < cargo.getCapacity(); index++) {
                        BlockType block = cargo.getBlockAt(index);
                        if(block != null) {
                            cargoBlocks.add(new BlockInfo(cargo, index, block.getValue()));
                        }
                    }
                }
            }
        }
        cargoBlocks.sort((a, b) -> Integer.compare(b.value, a.value));
        int remainingToRemove = quantity;
        for(BlockInfo cargo: cargoBlocks) {
            if(!(remainingToRemove > 0)) break;
            cargo.tile.unloadBlock(cargo.index);
            remainingToRemove--;
        }
        if(remainingToRemove > 0) {
            for(Tile[] row : tiles) {
                for(Tile tile : row) {
                    if(tile instanceof BatteriesTile batteryTile) {
                        while(remainingToRemove > 0 && batteryTile.getStoredBatteries() > 0) {
                            batteryTile.removeBattery();
                            remainingToRemove--;
                        }
                    }
                    if(remainingToRemove == 0) return;
                }
            }
        }
    }

    private Set<Point> getTilesPositions(Class<? extends Tile> tileClass, Predicate<Tile> filter) {
        Set<Point> positions = new HashSet<>();
        for(int y = 0; y < tiles.length; y++) {
            for(int x = 0; x < tiles[0].length; x++) {
                Tile tile = tiles[y][x];
                if(tileClass.isInstance(tile) && filter.test(tile)) {
                    positions.add(new Point(x, y));
                }
            }
        }
        return positions;
    }
    public Set<Point> getCabinTilesThatCanHostAliens() {
        return getTilesPositions(CabinTile.class, tile -> ((CabinTile)tile).canHostAnAlienType());
    }

    public record BatteriesData(int x, int y, int batteries, int capacity, int rotation) {}
    public record CrewData(int x, int y, CrewType crew) {}
    public record CargoData(int x, int y, BlockType[] cargo, int capacity, int rotation) {}

    public record ShipContent(
            Set<BatteriesData> batteriesData,
            Set<CrewData> crewData,
            Set<CargoData> cargoData
    ) {}
    public ShipContent getShipContent() {
        Set<BatteriesData> batteries = new HashSet<>();
        Set<CrewData> crew = new HashSet<>();
        Set<CargoData> cargo = new HashSet<>();

        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[0].length; x++) {
                Tile tile = tiles[y][x];

                if (tile instanceof BatteriesTile batteryTile) {
                    batteries.add(new BatteriesData(x, y, batteryTile.getStoredBatteries(), batteryTile.getCapacity(), batteryTile.getRotation()));
                } else if (tile instanceof CabinTile cabin) {
                    crew.add(new CrewData(x, y, cabin.getCrew()));
                } else if (tile instanceof CargoTile cargoTile) {
                    cargo.add(new CargoData(x, y, cargoTile.getContent(), cargoTile.getCapacity(), cargoTile.getRotation()));
                }
            }
        }
        return new ShipContent(batteries, crew, cargo);
    }

    public int getTileCount(Class<? extends Tile> tileClass, Predicate<Tile> filter) {
        int count = 0;
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(tileClass.isInstance(tile) && filter.test(tile)) count++;
            }
        }
        return count;
    }
    public int getTotalTileCount() {
        return getTileCount(Tile.class, tile -> !(tile instanceof EmptyTile));
    }

    public boolean hasTileOnPath(Projectile.Source source, int index, Class<? extends Tile> tileClass, Predicate<Tile> filter) {
        if(source == Projectile.Source.UP || source == Projectile.Source.DOWN) {
            if(index < 0 || index >= tiles[0].length) return false;
            for(Tile[] row : tiles) {
                if(tileClass.isInstance(row[index]) && filter.test(row[index])) return true;
            }
        }
        else if(source == Projectile.Source.LEFT || source == Projectile.Source.RIGHT) {
            if(index < 0 || index >= tiles.length) return false;
            for(Tile tile : tiles[index]) {
                if(tileClass.isInstance(tile) && filter.test(tile)) return true;
            }
        }
        return false;
    }

    public boolean isPathEmpty(Projectile.Source source, int index) {
        return !hasTileOnPath(source, index, Tile.class, tile -> !(tile instanceof EmptyTile));
    }
    public enum DefendingCannonType {
        NO_CANNON, SINGLE, DOUBLE;
    }
    public DefendingCannonType getDefendingCannonType(Projectile.Source source, int index) {
        if(source == Projectile.Source.UP) {
            if(hasTileOnPath(source, index, CannonTile.class, tile -> tile.getRotation() == source.toInt() && !((CannonTile)tile).isDouble())) return DefendingCannonType.SINGLE;
            if(hasTileOnPath(source, index, CannonTile.class, tile -> tile.getRotation() == source.toInt() && ((CannonTile)tile).isDouble())) return DefendingCannonType.DOUBLE;
        }
        else {
            for(int i = -1; i <= 1; i++) {
                if(hasTileOnPath(source, index + i, CannonTile.class, tile -> tile.getRotation() == source.toInt() && !((CannonTile)tile).isDouble()))
                    return DefendingCannonType.SINGLE;
            }
            for(int i = -1; i <= 1; i++) {
                if(hasTileOnPath(source, index + i, CannonTile.class, tile -> tile.getRotation() == source.toInt() && ((CannonTile)tile).isDouble()))
                    return DefendingCannonType.DOUBLE;
            }
        }
        return DefendingCannonType.NO_CANNON;
    }

    private Point getFirstNonEmptyTilePosition(Projectile.Source source, int index) {
        if(isPathEmpty(source, index)) return null;

        if(source == Projectile.Source.UP) {
            for(int y = 0; y < tiles.length; y++) {
                if(!(tiles[y][index] instanceof EmptyTile)) return new Point(index, y);
            }
        } else if(source == Projectile.Source.LEFT) {
            for(int x = 0; x < tiles[0].length; x++) {
                if(!(tiles[index][x] instanceof EmptyTile)) return new Point(x, index);
            }
        } else if(source == Projectile.Source.DOWN) {
            for(int y = tiles.length - 1; y >= 0; y--) {
                if(!(tiles[y][index] instanceof EmptyTile)) return new Point(index, y);
            }
        } else if(source == Projectile.Source.RIGHT) {
            for(int x = tiles[0].length - 1; x >= 0; x--) {
                if(!(tiles[index][x] instanceof EmptyTile)) return new Point(x, index);
            }
        }
        // Should not be able to reach here.
        throw new RuntimeException("Error while getting first non empty tile position");
    }
    public Point removeFirstTileInPath(Projectile.Source source, int index) {
        Point pos = getFirstNonEmptyTilePosition(source, index);
        if(pos != null) removeTile(pos.x, pos.y);
        return pos;
    }
    public boolean hasExposedConnectorInPath(Projectile.Source source, int index) {
        Point pos = getFirstNonEmptyTilePosition(source, index);
        if(pos == null) return false;
        return getTile(pos.x, pos.y).getConnector(source.toInt()) != Connector.NONE;
    }

    private boolean areConnectorsCompatible(Connector a, Connector b) {
        if(a == Connector.NONE && b == Connector.NONE) return true;
        if(a == Connector.NONE || b == Connector.NONE) return false;
        if(a == b) return true;
        return a == Connector.UNIVERSAL || b == Connector.UNIVERSAL;
    }

    private boolean hasValidConnections(int x, int y) {
        // Checks the connection in all 4 directions to see if its compatible.
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
        }
        return true;
    }

    protected boolean isInsideShip(int x, int y) {
        // Returns true if(x,y) is inside the usable space of the spaceship.
        if(y < 0 || x < 0 || y >= tiles.length || x >= tiles[0].length) return false;
        if(shipType == BoardType.LEARNING || shipType == BoardType.LEVEL_ONE) {
            int[][] type1 = {
                    {0, 0, 0, 1, 0, 0, 0},
                    {0, 0, 1, 1, 1, 0, 0},
                    {0, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 0, 1, 1, 0}
            };
            return type1[y][x] == 1;
        }
        if(shipType == BoardType.LEVEL_TWO) {
            int[][] type2 = {
                    {0, 0, 1, 0, 1, 0, 0},
                    {0, 1, 1, 1, 1, 1, 0},
                    {1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 0, 1, 1, 1}
            };
            return type2[y][x] == 1;
        }
        throw new RuntimeException("Invalid ship type: " + shipType);
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

                    for(int i = 0; i < 4; i++) {
                        if(cabin.getConnector(i) == Connector.NONE) continue;
                        int nx = x + offsets[i][1];
                        int ny = y + offsets[i][0];
                        if(!isInsideShip(nx, ny)) continue;

                        if(tiles[ny][nx] instanceof AlienSupportTile support) {
                            if(support.getAlienColor() == BROWN_ALIEN) canHostBrown = true;
                            if(support.getAlienColor() == PURPLE_ALIEN) canHostPurple = true;
                        }
                    }
                    cabin.allowAlienType(BROWN_ALIEN, canHostBrown);
                    cabin.allowAlienType(PURPLE_ALIEN, canHostPurple);
                }
            }
        }
    }

    public Set<Point> getInvalidTiles() {
        // Adds all invalid tile's coordinates to invalidTiles.
        Set<Point> invalidTiles = new HashSet<>();

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
                    int tileRotation = tile.getRotation();
                    if(tile instanceof EngineTile) tileRotation = (tileRotation + 2) % 4;

                    int[][] offsets = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
                    int nx = x + offsets[tileRotation][1];
                    int ny = y + offsets[tileRotation][0];
                    if(isInsideShip(nx, ny) && !(tiles[ny][nx] instanceof EmptyTile)) {
                        invalidTiles.add(new Point(x, y));
                        invalidTiles.add(new Point(nx, ny));
                        continue;
                    }
                }
                if(!hasValidConnections(x,y)) {
                    invalidTiles.add(new Point(x, y));
                }
            }
        }
        //checkConnectivity();
        return invalidTiles;
    }

//    private boolean isConnectedToType(int x, int y, Class<? extends Tile> type) {
//        if(!isInsideShip(x, y)) return false;
//
//        Tile tile = getTile(x, y);
//        int[][] offsets = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
//
//        for(int i = 0; i < 4; i++) {
//            if(tile.getConnector(i) == Connector.NONE) continue;
//            int nx = x + offsets[i][1];
//            int ny = y + offsets[i][0];
//            if(!isInsideShip(nx, ny)) continue;
//
//            if(type.isInstance(getTile(nx, ny))) return true;
//        }
//        return false;
//    }

    //List<List<Point>> groupsToChooseFrom;

    public void updateConnectivity() {
        // It uses a flood fill search to divide all the tiles in groups.
        // If there are more than one group, it automatically removes the ones without humans
        // and lets the player decide which one (with humans) to keep.
        boolean[][] visited = new boolean[tiles.length][tiles[0].length];

        List<List<Point>> groupsWithHumans = new ArrayList<>();
        List<List<Point>> groupsWithoutHumans = new ArrayList<>();
        for(int y = 0; y < tiles.length; y++) {
            for(int x = 0; x < tiles[0].length; x++) {
                if(!visited[y][x] && !(tiles[y][x] instanceof EmptyTile)) {
                    List<Point> group = new ArrayList<>();
                    floodFill(x, y, visited, group);
                    if(groupHasHumans(group)) {
                        groupsWithHumans.add(group);
                    } else {
                        groupsWithoutHumans.add(group);
                    }
                }
            }
        }
        if(groupsWithHumans.size() + groupsWithoutHumans.size() <= 1) return;

        // If there are both groups with crew and without humans, it removes the ones without.
        // It doesn't always delete the empty groups because if the last humans are removed, it would delete the ship.
        if(!groupsWithHumans.isEmpty()) {
            for(List<Point> group : groupsWithoutHumans) {
                removeGroup(group);
            }
            if(groupsWithHumans.size() > 1) {
                //groupsToChooseFrom = groupsWithHumans;

                // Find the group with the maximum number tiles
                List<Point> largestGroup = null;
                int maxSize = -1;
                for (List<Point> group : groupsWithHumans) {
                    if (group.size() > maxSize) {
                        largestGroup = group;
                        maxSize = group.size();
                    }
                }

                // Remove all other human groups except the largest
                for (List<Point> group : groupsWithHumans) {
                    if (group != largestGroup) {
                        removeGroup(group);
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
            removedTiles++;
        }
    }

//    public void chooseGroupToKeep(int index) {
//        // Lets the player choose which group of tiles to keep.
//        if(index < 0 || index >= groupsToChooseFrom.size()) throw new IllegalArgumentException("index out of bounds.");
//        List<Point> groupToKeep = groupsToChooseFrom.get(index);
//        for(List<Point> group : groupsToChooseFrom) {
//            if(group != groupToKeep) {
//                removeGroup(group);
//            }
//        }
//    }
}