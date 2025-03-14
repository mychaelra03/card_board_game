package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.tiles.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class ShipBoardTest {

    @Test
    public void testInitializeEmptyTilesOnBoard1() {
        ShipBoard board = new ShipBoard(1);

        for (Tile[] row : board.getTiles()) {
            for (Tile tile : row) {
                assertTrue(tile instanceof EmptyTile, "Tile should be empty after initialization.");
            }
        }
    }
    @Test
    public void testInitializeEmptyTilesOnBoard2() {
        ShipBoard board = new ShipBoard(2);

        for (Tile[] row : board.getTiles()) {
            for (Tile tile : row) {
                assertTrue(tile instanceof EmptyTile, "Tile should be empty after initialization.");
            }
        }
    }

    @Test
    public void testSetTileAndRetrieveIt() {
        ShipBoard board = new ShipBoard(1);
        Tile engine = new EngineTile(0, 0, 0, 0, 0);

        board.setTile(2, 3, engine, 0);
        Tile result = board.getTile(2, 3);

        assertEquals(engine, result, "The tile retrieved should be the same as the one set.");
    }

    @Test
    public void testInvalidPlacementOnOccupiedTile() {
        ShipBoard board = new ShipBoard(1);
        board.setTile(2, 2, new EngineTile(0, 0, 0, 0, 0), 0);

        assertThrows(IllegalArgumentException.class, () -> {
            board.setTile(2, 2, new CabinTile(0, 0, 0, 0, 0), 0);
        }, "Setting a tile on an occupied space should throw.");
    }
}
