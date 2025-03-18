package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.tiles.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ShipBoardTest {

    @Test
    void testInitializationBoard1() {
        ShipBoard board = new ShipBoard(1);
        Tile[][] tiles = board.getTiles();
        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[y].length; x++) {
                if (board.isInsideShip(x, y)) {
                    assertInstanceOf(EmptyTile.class, tiles[y][x], "Tile should be EmptyTile after initialization.");
                }
            }
        }
    }

    @Test
    void testInitializationBoard2() {
        ShipBoard board = new ShipBoard(2);
        Tile[][] tiles = board.getTiles();
        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[y].length; x++) {
                if (board.isInsideShip(x, y)) {
                    assertInstanceOf(EmptyTile.class, tiles[y][x], "Tile should be EmptyTile after initialization.");
                }
            }
        }
    }

    @Test
    void testIsInsideShipType1() {
        ShipBoard board = new ShipBoard(1);
        assertTrue(board.isInsideShip(3, 2), "Coordinate (3,2) should be inside the ship for type 1");
        assertFalse(board.isInsideShip(0, 0), "Coordinate (0,0) should be outside the ship for type 1");
    }

    @Test
    void testSetTileValid() {
        ShipBoard board = new ShipBoard(1);
        CabinTile cabin = new CabinTile(0, 0, 0, 0, 0);
        board.setTile(3, 2, cabin, 1);
        assertEquals(cabin, board.getTile(3, 2), "The tile should be the cabin.");
        assertEquals(1, cabin.getRotation(), "The tile rotation should be 1.");
    }

    @Test
    void testSetTileOccupied() {
        ShipBoard board = new ShipBoard(1);
        CabinTile firstTile = new CabinTile(0, 0, 0, 0, 0);
        CabinTile secondTile = new CabinTile(0, 0, 0, 0, 0);
        board.setTile(3, 2, firstTile, 0);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> board.setTile(3, 2, secondTile, 0));
        assertTrue(exception.getMessage().contains("Tile occupied"), "Exception message should indicate tile occupation.");
    }

    @Test
    void testGetBatteriesCount() {
        ShipBoard board = new ShipBoard(1);
        BatteriesTile batteries = new BatteriesTile(0, 0, 0, 0, 0, 3);
        board.setTile(3, 2, batteries, 0);
        assertEquals(3, board.getTotalBatteriesCount(), "Batteries count should be 3.");
    }

    @Test
    void testRemoveCrewMemberAt() {
        ShipBoard board = new ShipBoard(1);
        CabinTile cabin = new CabinTile(0, 0, 0, 0, 0);
        cabin.setCrewType(CrewType.HUMAN);
        board.setTile(3, 2, cabin, 0);
        assertEquals(2, board.getTotalHumanCount(), "Human count should be 2.");
        board.removeCrewMemberAt(3, 2, 0);
        assertEquals(1, board.getTotalHumanCount(), "Human count should be 1.");
        board.removeCrewMemberAt(3, 2, 1);
        assertEquals(0, board.getTotalHumanCount(), "Human count should be 0.");
        assertNull(board.removeCrewMemberAt(3,2,0), "Crew member should not exist.");
    }

    @Test
    void testHasAlien() {
        ShipBoard board = new ShipBoard(1);
        assertFalse(board.hasAlien(CrewType.BROWN_ALIEN), "Should not have a brown alien initially.");
        CabinTile cabin = new CabinTile(0,0,0,0,0);
        cabin.allowAlienType(CrewType.BROWN_ALIEN,true);
        cabin.setCrewType(CrewType.BROWN_ALIEN);
        board.setTile(3, 2, cabin, 0);
        assertTrue(board.hasAlien(CrewType.BROWN_ALIEN), "Should have a brown alien after setting one.");
        board.setCrewTypeAt(3, 2, CrewType.HUMAN);
        assertFalse(board.hasAlien(CrewType.BROWN_ALIEN), "The alien should have been removed.");
    }

    @Test
    void testGetTotalCannonStrength() {
        ShipBoard board = new ShipBoard(1);
        assertEquals(0, board.getTotalCannonStrength(), "Total cannon strength should be 0.");
        CannonTile cannon = new CannonTile(0,0,0,0,0);
        board.setTile(3, 2, cannon, 0);
        assertEquals(1, board.getTotalCannonStrength(), "Total cannon strength should be 1.");
        CannonTile rotatedCannon = new CannonTile(0,0,0,0,0);
        board.setTile(3, 3, rotatedCannon, 1);
        assertEquals(1.5, board.getTotalCannonStrength(), "Total cannon strength should be 1.5.");
    }

    @Test
    void testGetTotalEngineStrength() {
        ShipBoard board = new ShipBoard(1);
        assertEquals(0, board.getTotalEngineStrength(), "Total engine strength should be 0.");
        EngineTile engine = new EngineTile(0,0,0,0,0);
        board.setTile(3, 2, engine, 0);
        assertEquals(1, board.getTotalEngineStrength(), "Total engine strength should be 1.");
    }

    @Test
    void testCalculateCargoValue() {
        ShipBoard board = new ShipBoard(1);
        CargoTile cargo = new CargoTile(0,0,0,0,0, CargoTile.CargoType.NORMAL, 3);
        board.setTile(3, 2, cargo, 0);
        board.loadCargoAt(3, 2, 0, BlockType.YELLOW);
        board.loadCargoAt(3, 2, 2, BlockType.BLUE);
        assertEquals(4, board.getTotalHoldsValue(), "Cargo value should be 4.");
        assertEquals(BlockType.YELLOW, board.loadCargoAt(3,2,0,null), "Should return the removed hold.");
    }

    @Test
    void testGetExposedConnectorsCount() {
        ShipBoard board = new ShipBoard(1);
        CannonTile cannon = new CannonTile(0,Connector.SINGLE,Connector.SINGLE,Connector.SINGLE,Connector.SINGLE);
        CabinTile cabin = new CabinTile(1,Connector.SINGLE,Connector.SINGLE,Connector.SINGLE,Connector.SINGLE);
        board.setTile(3, 2, cannon, 0);
        assertEquals(4, board.getExposedConnectorsCount(), "Exposed connectors should be 4.");
        board.setTile(3, 3, cabin, 0);
        assertEquals(6, board.getExposedConnectorsCount(), "Exposed connectors should be 6.");
    }

    @Test
    void testConnectorsValidationCheck() {
        StructuralTile tileNone = new StructuralTile(0, 0,0,0,0);
        StructuralTile tileSingle = new StructuralTile(0, 1,1,1,1);
        StructuralTile tileDouble = new StructuralTile(0, 2,2,2,2);
        StructuralTile tileUniv = new StructuralTile(0, 3,3,3,3);
        ShipBoard board0 = new ShipBoard(1);
        board0.setTile(1, 2, tileNone, 0);
        assertDoesNotThrow(() -> board0.setTile(2, 2, tileNone, 0));
        assertThrows(IllegalArgumentException.class, () -> board0.setTile(3, 2, tileSingle, 0));
        Exception exception0 = assertThrows(IllegalArgumentException.class, () -> board0.setTile(3, 2, tileDouble, 0));
        assertTrue(exception0.getMessage().contains("connectors don't match"), "Exception message should indicate invalid connection.");
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> board0.setTile(3, 2, tileUniv, 0));
        assertTrue(exception1.getMessage().contains("connectors don't match"), "Exception message should indicate invalid connection.");
        // Currently it throws after placing, wrong
        ShipBoard board1 = new ShipBoard(1);
        board1.setTile(1, 2, tileSingle, 0);
        assertDoesNotThrow(() -> board1.setTile(2, 2, tileUniv, 0));
        assertDoesNotThrow(() -> board1.setTile(3, 2, tileDouble, 0));
    }

    @Test
    void testConnectorsValidationCheckWithRotation() {
        ShipBoard board = new ShipBoard(1);
        board.setTile(1, 2, new StructuralTile(0, 1,0,0,0), 1);
        assertDoesNotThrow(() -> board.setTile(2, 2, new StructuralTile(0, 1,0,0,0), 3));
        assertDoesNotThrow(() -> board.setTile(2, 1, new StructuralTile(0, 1,0,0,0), 0));
        assertThrows(IllegalArgumentException.class, () -> board.setTile(3, 2, new StructuralTile(0, 1,0,0,0), 3));
    }

    @Test
    void testTileUpdateAlienSupport() {
        ShipBoard board = new ShipBoard(1);
        CabinTile cabin = new CabinTile(0,3,3,3,0);
        AlienSupportTile alienSuppNotConnected = new AlienSupportTile(0, 0, 0, 0, 0, CrewType.BROWN_ALIEN);
        AlienSupportTile alienSuppDifferentType = new AlienSupportTile(0, 3, 3, 3, 3, CrewType.PURPLE_ALIEN);
        AlienSupportTile alienSupp = new AlienSupportTile(0, 3, 3, 3, 3, CrewType.BROWN_ALIEN);
        AlienSupportTile alienSupp2 = new AlienSupportTile(0, 3, 3, 3, 3, CrewType.BROWN_ALIEN);
        board.setTile(3, 2, cabin, 0);
        assertFalse(((CabinTile)board.getTile(3,2)).canHostAlienType(CrewType.BROWN_ALIEN), "The cabin should not be able to support aliens yet.");
        board.setTile(2, 2, alienSuppNotConnected, 0);
        assertFalse(((CabinTile)board.getTile(3,2)).canHostAlienType(CrewType.BROWN_ALIEN), "The cabin should not be able to support aliens yet.");
        board.setTile(3, 3, alienSuppDifferentType, 0);
        assertFalse(((CabinTile)board.getTile(3,2)).canHostAlienType(CrewType.BROWN_ALIEN), "The cabin should not be able to support brown aliens yet.");
        board.setTile(3, 1, alienSupp, 0);
        assertTrue(((CabinTile)board.getTile(3,2)).canHostAlienType(CrewType.BROWN_ALIEN), "The cabin should now be able to support aliens.");
        board.setTile(4, 2, alienSupp2, 0);
        assertTrue(((CabinTile)board.getTile(3,2)).canHostAlienType(CrewType.BROWN_ALIEN), "The cabin should still be able to support aliens.");
    }

    @Test
    void testTileRemoveAlienSupport() {
        ShipBoard board = new ShipBoard(1);
        CabinTile cabin = new CabinTile(0,3,3,3,0);
        AlienSupportTile alienSuppToRemove = new AlienSupportTile(0, 3, 3, 3, 3, CrewType.BROWN_ALIEN);
        AlienSupportTile alienSuppNotConnected = new AlienSupportTile(0, 0, 0, 0, 0, CrewType.BROWN_ALIEN);
        AlienSupportTile alienSuppStillConnected = new AlienSupportTile(0, 3, 3, 3, 3, CrewType.BROWN_ALIEN);
        AlienSupportTile alienSuppDifferentType = new AlienSupportTile(0, 3, 3, 3, 3, CrewType.PURPLE_ALIEN);
        board.setTile(3, 2, cabin, 0);
        board.setTile(3, 1, alienSuppToRemove, 0);
        board.setTile(2, 2, alienSuppNotConnected, 0);
        board.setTile(4, 2, alienSuppStillConnected, 0);
        board.setTile(3, 3, alienSuppDifferentType, 0);
        board.setCrewTypeAt(3, 2, CrewType.BROWN_ALIEN);
        assertTrue(board.hasAlienAt(3, 2, CrewType.BROWN_ALIEN), "There should be a brown alien inside the cabin.");
        board.removeTile(3, 1);
        assertTrue(board.hasAlienAt(3, 2, CrewType.BROWN_ALIEN), "There should still be a brown alien inside the cabin.");
        assertInstanceOf(EmptyTile.class, board.getTile(3, 1), "The removed tile should have become an EmptyTile.");
        board.removeTile(4, 2);
        assertFalse(board.hasAlienAt(3, 2, CrewType.BROWN_ALIEN), "The alien should have been removed, there are no more brown supporting tiles.");
    }
}
