package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Server.Model.cards.*;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.cards.projectile.CannonShot;
import it.polimi.galaxy_trucker.Server.Model.cards.projectile.Projectile;
import it.polimi.galaxy_trucker.Server.Model.tiles.*;
import it.polimi.galaxy_trucker.ShipBoard;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

import static it.polimi.galaxy_trucker.BoardType.*;
import static it.polimi.galaxy_trucker.Server.Model.cards.CombatZoneCard.CombatRule.*;
import static it.polimi.galaxy_trucker.Server.Model.tiles.BlockType.*;
import static it.polimi.galaxy_trucker.Server.Model.tiles.Connector.*;
import static org.junit.jupiter.api.Assertions.*;

class CardsTest {

    public static final Connector[] FULL_UNIVERSAL_CONNECTORS = {UNIVERSAL, UNIVERSAL, UNIVERSAL, UNIVERSAL};

    static private ShipBoard genericShipBoard(int id){

        //1st shipB
        ShipBoard shipBoard1 = new ShipBoard(LEVEL_TWO);

        shipBoard1.setTile(4,2,58,0);
        shipBoard1.setTile(5,2,45,0);
        shipBoard1.setTile(6,2,26,3);
        shipBoard1.setTile(2,2,46,3);
        shipBoard1.setTile(1,2,132,0);
        shipBoard1.setTile(0,2,63,2);
        shipBoard1.setTile(1,1,104,0);
        shipBoard1.setTile(2,1,30,1);
        shipBoard1.setTile(3,1,59,0);
        shipBoard1.setTile(4,1,120,0);
        shipBoard1.setTile(5,1,131,0);
        shipBoard1.setTile(2,0,96,0);
        shipBoard1.setTile(0,3,8,2);
        shipBoard1.setTile(1,3,148,0);
        shipBoard1.setTile(2,3,56,3);
        shipBoard1.setTile(3,3,80,0);
        shipBoard1.setTile(4,3,49,2);
        shipBoard1.setTile(5,3,43,2);
        shipBoard1.setTile(6,3,14,3);
        shipBoard1.setTile(0,4,67,0);
        shipBoard1.setTile(1,4,78,0);
        shipBoard1.setTile(2,4,93,0);
        shipBoard1.setTile(4,4,88,0);
        shipBoard1.setTile(5,4,85,0);
        shipBoard1.setTile(6,4,84,0);

        // 2nd shipB
        ShipBoard shipBoard2 = new ShipBoard(LEVEL_TWO);

        shipBoard2.setTile(3,3,80,0);
        shipBoard2.setTile(3,1,105,0);
        shipBoard2.setTile(4,2,144,0);
        shipBoard2.setTile(5,2,40,2);
        shipBoard2.setTile(5,1,119,0);
        shipBoard2.setTile(6,2,113,0);
        shipBoard2.setTile(4,3,24,3);
        shipBoard2.setTile(2,2,149,2);
        shipBoard2.setTile(4,4,70,0);
        shipBoard2.setTile(5,3,2,3);
        shipBoard2.setTile(6,3,21,0);
        shipBoard2.setTile(5,4,75,0);
        shipBoard2.setTile(6,4,13,3);
        shipBoard2.setTile(4,1,17,3);
        shipBoard2.setTile(4,0,96,0);
        shipBoard2.setTile(2,1,53,3);
        shipBoard2.setTile(2,0,125,0);
        shipBoard2.setTile(1,2,32,3);
        shipBoard2.setTile(1,3,46,0);
        shipBoard2.setTile(0,3,136,2);
        shipBoard2.setTile(1,1,98,3);
        shipBoard2.setTile(2,3,88,0);
        shipBoard2.setTile(1,4,78,0);
        shipBoard2.setTile(0,4,67,0);

        //3rd ship
        ShipBoard shipBoard3 = new ShipBoard(LEVEL_TWO);

        shipBoard3.setTile(4,2,32,0);
        shipBoard3.setTile(3,1,122,0);
        shipBoard3.setTile(3,3,102,2);
        shipBoard3.setTile(2,2,41,2);
        shipBoard3.setTile(2,3,146,0);
        shipBoard3.setTile(4,3,136,1);
        shipBoard3.setTile(1,3,6,1);
        shipBoard3.setTile(4,4,73,0);
        shipBoard3.setTile(5,4,36,0);
        shipBoard3.setTile(6,4,137,0);
        shipBoard3.setTile(6,3,38,1);
        shipBoard3.setTile(5,3,31,2);
        shipBoard3.setTile(6,2,103,1);
        shipBoard3.setTile(5,2,58,1);
        shipBoard3.setTile(5,1,128,0);
        shipBoard3.setTile(4,1,1,0);
        shipBoard3.setTile(4,0,131,3);
        shipBoard3.setTile(2,4,95,0);
        shipBoard3.setTile(1,4,15,3);
        shipBoard3.setTile(0,4,88,0);
        shipBoard3.setTile(0,3,126,3);
        shipBoard3.setTile(0,2,108,3);
        shipBoard3.setTile(1,2,46,0);
        shipBoard3.setTile(1,1,102,1);

        // 4th shipB
        ShipBoard shipBoard4 = new ShipBoard(LEVEL_TWO);

        shipBoard4.setTile(4,2,33,0);
        shipBoard4.setTile(4,1,121,0);
        shipBoard4.setTile(5,2,61,0);
        shipBoard4.setTile(6,2,8,0);
        shipBoard4.setTile(2,2,6,0);
        shipBoard4.setTile(3,1,128,0);
        shipBoard4.setTile(2,1,18,1);
        shipBoard4.setTile(2,0,16,3);
        shipBoard4.setTile(5,1,101,0);
        shipBoard4.setTile(4,3,46,0);
        shipBoard4.setTile(4,4,140,1);
        shipBoard4.setTile(5,3,74,0);
        shipBoard4.setTile(6,3,13,3);
        shipBoard4.setTile(1,1,120,0);
        shipBoard4.setTile(3,3,76,0);
        shipBoard4.setTile(2,3,50,1);
        shipBoard4.setTile(2,4,95,0);
        shipBoard4.setTile(1,4,79,0);
        shipBoard4.setTile(0,4,103,0);
        shipBoard4.setTile(1,2,10,2);
        shipBoard4.setTile(1,3,65,1);

        if(id == 1) return shipBoard1;
        if(id == 2) return shipBoard2;
        if(id == 3) return shipBoard3;
        if(id == 4) return shipBoard4;
        throw new IllegalArgumentException("invalid id: " + id + ".");
    }


    @Test
    void epidemicCard() {
        Player player1 = new Player("TestPlayer1", new int[]{0, 0});
        Player player2 = new Player("TestPlayer2", new int[]{0, 0});
        Player player3 = new Player("TestPlayer3", new int[]{0, 0});
        Player player4 = new Player("TestPlayer4", new int[]{0, 0});
        player1.setNewShipBoard(LEVEL_TWO);
        player2.setNewShipBoard(LEVEL_TWO);
        player3.setNewShipBoard(LEVEL_TWO);
        player4.setNewShipBoard(LEVEL_TWO);
        // 1st case
        player1.getShipBoard().setTile(2,2, new CabinTile(0,FULL_UNIVERSAL_CONNECTORS),0);

        // 2nd case
        player2.getShipBoard().setTile(2,2, new CabinTile(0,FULL_UNIVERSAL_CONNECTORS),0);
        player2.getShipBoard().setTile(1,2, new CabinTile(0,FULL_UNIVERSAL_CONNECTORS),0);

        // 3rd  case
        player3.getShipBoard().setTile(3,3, new CabinTile(0,new Connector[]{UNIVERSAL, UNIVERSAL, UNIVERSAL,Connector.NONE}),0);
        player3.getShipBoard().setTile(2,2, new StructuralTile(0,new Connector[]{UNIVERSAL, UNIVERSAL, UNIVERSAL,Connector.NONE}),0);
        player3.getShipBoard().setTile(2,3, new CabinTile(0,new Connector[]{UNIVERSAL,Connector.NONE, UNIVERSAL, UNIVERSAL}),0);

        // 4th case
        player4.getShipBoard().setTile(3,1, new BatteriesTile(0,FULL_UNIVERSAL_CONNECTORS,3),0);
        player4.getShipBoard().setTile(4,1, new CabinTile(0,FULL_UNIVERSAL_CONNECTORS),0);
        player4.getShipBoard().setTile(4,2, new CabinTile(0,FULL_UNIVERSAL_CONNECTORS),0);
        player4.getShipBoard().setTile(4,3, new CabinTile(0,FULL_UNIVERSAL_CONNECTORS),0);
        player4.getShipBoard().setTile(5,3, new CargoTile(0,FULL_UNIVERSAL_CONNECTORS, CargoTile.CargoType.RED,1),0);
        player4.getShipBoard().setTile(6,3, new CabinTile(0,FULL_UNIVERSAL_CONNECTORS),0);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);

        GameModel gameModel = new GameModel(LEVEL_TWO, players);

        EpidemicCard epidemicCard = new EpidemicCard(0, 1);

        epidemicCard.assignBoard(gameModel);
        epidemicCard.activate();

        assertEquals(2, player1.getCrewCount(),"Crew count should be: 2 ");
        assertEquals(3, player2.getCrewCount(),"Crew count should be: 3 ");
        assertEquals(4, player3.getCrewCount(),"Crew count should be: 4 ");
        assertEquals(6, player4.getCrewCount(),"Crew count should be: 6 ");

        assertTrue(epidemicCard.isFinished(), "The card should be finished.");
    }

    @Test
    void starDustCard() {

        Player player1 = new Player("TestPlayer1", new int[]{0, 0});
        Player player2 = new Player("TestPlayer2", new int[]{0, 0});
        Player player3 = new Player("TestPlayer3", new int[]{0, 0});
        Player player4 = new Player("TestPlayer4", new int[]{0, 0});
        player1.setNewShipBoard(LEVEL_TWO);
        player2.setNewShipBoard(LEVEL_TWO);
        player3.setNewShipBoard(LEVEL_TWO);
        player4.setNewShipBoard(LEVEL_TWO);

        // 1st case
        player1.getShipBoard().setTile(2,2, new CabinTile(0,FULL_UNIVERSAL_CONNECTORS),0);

        // 2nd case
        player2.getShipBoard().setTile(2,2, new CabinTile(0,FULL_UNIVERSAL_CONNECTORS),0);
        player2.getShipBoard().setTile(1,2, new CabinTile(0,FULL_UNIVERSAL_CONNECTORS),0);

        // 3rd  case
        player3.getShipBoard().setTile(3,3, new CabinTile(0,new Connector[]{UNIVERSAL,Connector.NONE, UNIVERSAL,Connector.NONE}),0);
        player3.getShipBoard().setTile(2,2, new StructuralTile(0,new Connector[]{UNIVERSAL, UNIVERSAL, UNIVERSAL,Connector.NONE}),0);
        player3.getShipBoard().setTile(2,3, new CabinTile(0,new Connector[]{UNIVERSAL,Connector.NONE, UNIVERSAL, UNIVERSAL}),0);

        // 4th case
        player4.getShipBoard().setTile(3,1, new BatteriesTile(0,FULL_UNIVERSAL_CONNECTORS,3),0);
        player4.getShipBoard().setTile(4,1, new CabinTile(0,FULL_UNIVERSAL_CONNECTORS),0);
        player4.getShipBoard().setTile(4,2, new CabinTile(0,FULL_UNIVERSAL_CONNECTORS),0);
        player4.getShipBoard().setTile(4,3, new CabinTile(0,FULL_UNIVERSAL_CONNECTORS),0);
        player4.getShipBoard().setTile(5,3, new CargoTile(0,FULL_UNIVERSAL_CONNECTORS, CargoTile.CargoType.RED,1),0);
        player4.getShipBoard().setTile(6,3, new CabinTile(0,new Connector[]{UNIVERSAL, UNIVERSAL,Connector.NONE, UNIVERSAL}),0);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);

        GameModel gameModel = new GameModel(LEVEL_TWO, players);

        player1.setPosition(10);
        player2.setPosition(1);
        player3.setPosition(7);
        player4.setPosition(5);

        StarDustCard starDustCard = new StarDustCard(24,1);
        starDustCard.assignBoard(gameModel);

        starDustCard.activate();

        assertEquals(6, player1.getExposedConnectorsCount(), "Player1 should have 6 exposed connectors.");
        assertEquals(8, player2.getExposedConnectorsCount(), "Player2 should have 8 exposed connectors.");
        assertEquals(6, player3.getExposedConnectorsCount(), "Player3 should have 6 exposed connectors.");
        assertEquals(13, player4.getExposedConnectorsCount(), "Player4 should have 13 exposed connectors.");

        assertEquals(4, player1.getPosition(),"Player1 should end up in position 2");
        assertEquals(-7, player2.getPosition(),"Player2 should end up in position -8");
        assertEquals(1, player3.getPosition(),"Player3 should end up in position -2");
        assertEquals(-9, player4.getPosition(),"Player4 should end up in position -10");

        assertTrue(starDustCard.isFinished(), "The card should be finished.");
    }

    @Test
    void combatZone() {

        CannonShot[] cannonShots = new CannonShot[]{
                new CannonShot(Projectile.Size.SMALL, Projectile.Source.UP, 1),
                new CannonShot(Projectile.Size.LARGE,Projectile.Source.UP, 1),
                new CannonShot(Projectile.Size.SMALL,Projectile.Source.DOWN, 1)
        };

        CombatZoneCard.CombatRule combatRule1 = new CombatZoneCard.CombatRule(Condition.CANNON_POWER,Punishment.FLIGHT_DAYS,3,null);

        CombatZoneCard.CombatRule combatRule2 = new CombatZoneCard.CombatRule(Condition.ENGINE_POWER,Punishment.CANNON_SHOTS,3,cannonShots);

        CombatZoneCard.CombatRule combatRule3 = new CombatZoneCard.CombatRule(Condition.CREW_SIZE,Punishment.CARGO_LOSS,3,null);

        CombatZoneCard.CombatRule combatRule4 = new CombatZoneCard.CombatRule(Condition.CANNON_POWER,Punishment.CREW_LOSS,3,null);

        CombatZoneCard.CombatRule[] combatRules = new CombatZoneCard.CombatRule[]{combatRule1,combatRule2,combatRule3,combatRule4};

        CombatZoneCard combatZoneCard = new CombatZoneCard(0,2,combatRules);

        Player player1 = new Player("Player1", new int[]{0,0}, genericShipBoard(1));
        Player player2 = new Player("Player2", new int[]{0,0}, genericShipBoard(2));
        Player player3 = new Player("Player3", new int[]{0,0}, genericShipBoard(3));
        Player player4 = new Player("Player4", new int[]{0,0}, genericShipBoard(4));

        player1.getShipBoard().setCrewTypeAt(2,2,CrewType.BROWN_ALIEN);
        player2.getShipBoard().setCrewTypeAt(1,3,CrewType.BROWN_ALIEN);
        player3.getShipBoard().setCrewTypeAt(4,2,CrewType.BROWN_ALIEN);
        player4.getShipBoard().setCrewTypeAt(4,3,CrewType.PURPLE_ALIEN);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);


        GameModel gameModel = new GameModel(LEVEL_TWO, players);

        // loading cargoHolds ship 1
        player1.getShipBoard().loadCargoAt(2,1, 0, BlockType.BLUE);
        // loading cargoHolds ship 2
        player2.getShipBoard().loadCargoAt(4,1, 0, BlockType.BLUE);
        // loading cargoHolds ship 3
        player3.getShipBoard().loadCargoAt(5,2, 0, BlockType.BLUE);
        // loading cargoHolds ship 4
        player4.getShipBoard().loadCargoAt(2,1, 1, BlockType.YELLOW);
        player4.getShipBoard().loadCargoAt(1,3, 1, BlockType.BLUE);

        gameModel.movePlayer(player4, 3, GameModel.Direction.FORWARD);      // 3
        gameModel.movePlayer(player3, 3, GameModel.Direction.FORWARD);      // 4
        gameModel.movePlayer(player2, 3, GameModel.Direction.FORWARD);      // 5
        gameModel.movePlayer(player1, 3, GameModel.Direction.FORWARD);      // 6

        assertEquals(6,player1.getPosition(),"The player should be at 6.");
        assertEquals(5,player2.getPosition(),"The player should be at 5.");
        assertEquals(4,player3.getPosition(),"The player should be at 4.");
        assertEquals(3,player4.getPosition(),"The player should be at 3.");

        assertEquals(1,player1.getShipBoard().getTotalHoldsValue());
        assertEquals(1,player2.getShipBoard().getTotalHoldsValue());
        assertEquals(1,player3.getShipBoard().getTotalHoldsValue());
        assertEquals(4,player4.getShipBoard().getTotalHoldsValue());
        assertEquals(12,player4.getShipBoard().getTotalBatteriesCount());

        // apply the card
        combatZoneCard.assignBoard(gameModel);
        combatZoneCard.activate();

        // rule 1
        combatZoneCard.activateCannons(player1, 0, 0, new ArrayList<>());
        combatZoneCard.activateCannons(player2, 0, 0, new ArrayList<>());
        combatZoneCard.activateCannons(player3, 0, 0, new ArrayList<>());
        combatZoneCard.activateCannons(player4, 0, 0, new ArrayList<>());
        // The worst player is player3
        assertEquals(6,player1.getPosition(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a 0");
        assertEquals(5,player2.getPosition(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a 0");
        assertEquals(0,player3.getPosition(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a 0");
        assertEquals(3,player4.getPosition(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a 0");

        // rule 2
        assertEquals(26, player1.getTotalTileCount());
        assertEquals(25, player2.getTotalTileCount());
        assertEquals(25, player3.getTotalTileCount());
        assertEquals(22, player4.getTotalTileCount());
        assertInstanceOf(CannonTile.class, player4.getShipBoard().getTile(0, 4), "player4 should have a Cannon at (0,4).");
        assertEquals(4,player4.getShipBoard().getTotalHoldsValue());
        assertEquals(12,player4.getShipBoard().getTotalBatteriesCount());

        combatZoneCard.activateEngines(player1, 0, new ArrayList<>());
        combatZoneCard.activateEngines(player2, 0, new ArrayList<>());
        combatZoneCard.activateEngines(player4, 0, new ArrayList<>());
        combatZoneCard.activateEngines(player3, 0, new ArrayList<>());
        // player3 and player4 have the same score (3), but player4 appear first.
        // player4 does not have shields to protect himself and loses 5 tiles.
        // It also loses 2 batteries and a blue cargo.
        assertEquals(26, player1.getTotalTileCount());
        assertEquals(25, player2.getTotalTileCount());
        assertEquals(25, player3.getTotalTileCount());
        assertEquals(17, player4.getTotalTileCount());
        assertInstanceOf(EmptyTile.class, player4.getShipBoard().getTile(0, 4), "player4 should not have a Cannon anymore at (0,4).");

        // rule 3
        assertEquals(7,player1.getCrewCount());
        assertEquals(7,player2.getCrewCount());
        assertEquals(11,player3.getCrewCount());
        assertEquals(5,player4.getCrewCount());
        // The worst player is player4
        // It removes the single yellow cargo (-3 total value) and 2 batteries.
        assertEquals(1,player1.getShipBoard().getTotalHoldsValue());
        assertEquals(1,player2.getShipBoard().getTotalHoldsValue());
        assertEquals(1,player3.getShipBoard().getTotalHoldsValue());
        assertEquals(0,player4.getShipBoard().getTotalHoldsValue());
        assertEquals(8,player4.getShipBoard().getTotalBatteriesCount());

        // rule 4
        assertFalse(combatZoneCard.isFinished(), "The card should not be finished.");

        combatZoneCard.activateCannons(player1, 0, 0, new ArrayList<>());
        combatZoneCard.activateCannons(player2, 0, 0, new ArrayList<>());
        combatZoneCard.activateCannons(player4, 0, 0, new ArrayList<>());
        combatZoneCard.activateCannons(player3, 0, 0, new ArrayList<>());
        // The worst player is player3
        // He gives up 3 crew members.
        combatZoneCard.removeMemberFrom(player3, new Point(1, 2), 0);
        combatZoneCard.removeMemberFrom(player3, new Point(1, 2), 1);
        combatZoneCard.removeMemberFrom(player3, new Point(3, 2), 0);
        assertEquals(7,player1.getCrewCount(),"Player 1 should still have 6 crew members.");
        assertEquals(7,player2.getCrewCount(),"Player 2 should still have 7 crew members.");
        assertEquals(8,player3.getCrewCount(),"Player 3 should now have 8 crew members.");
        assertEquals(5,player4.getCrewCount(),"Player 4 should still have 5 crew members.");

        // TODO: should try throwing exceptions.
        assertTrue(combatZoneCard.isFinished(), "The card should be finished.");
    }

    @Test
    void planetsCard() {

        Player player1 = new Player("Player1", new int[]{0,0}, genericShipBoard(1));
        Player player2 = new Player("Player2", new int[]{0,0}, genericShipBoard(2));
        Player player3 = new Player("Player3", new int[]{0,0}, genericShipBoard(3));
        Player player4 = new Player("Player4", new int[]{0,0}, genericShipBoard(4));
        player4.getShipBoard().loadCargoAt(1, 3, 0, YELLOW);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);

        GameModel gameModel = new GameModel(LEVEL_TWO, players);

        player1.setPosition(4);
        player2.setPosition(3);
        player3.setPosition(2);
        player4.setPosition(1);

        BlockType[][] blockTypes = {
                {RED, RED, RED, RED},
                {GREEN, GREEN, YELLOW, YELLOW},
                {GREEN, GREEN},
                {BLUE, BLUE}
        };
        PlanetsCard planetsCard = new PlanetsCard(0,2,blockTypes,3);
        planetsCard.assignBoard(gameModel);
        planetsCard.activate();

        assertNull(player1.getCargoPool());
        assertNull(player2.getCargoPool());
        assertNull(player3.getCargoPool());
        assertNull(player4.getCargoPool());

        planetsCard.choosePlanet(player1, 0);
        planetsCard.refusePlanet(player2);
        planetsCard.refusePlanet(player3);
        planetsCard.choosePlanet(player4, 3);

        assertEquals(0, player1.getCargoHoldsValue());
        assertEquals(0, player2.getCargoHoldsValue());
        assertEquals(0, player3.getCargoHoldsValue());
        assertEquals(3, player4.getCargoHoldsValue());

        assertEquals(List.of(RED, RED, RED, RED), player1.getCargoPool());
        assertNull(player2.getCargoPool());
        assertNull(player3.getCargoPool());
        assertEquals(List.of(BLUE, BLUE), player4.getCargoPool());

        planetsCard.placeCargo(player4, 0, new Point(1, 3), 0);
        assertEquals(List.of(BLUE, YELLOW), player4.getCargoPool());
        assertEquals(1, player4.getCargoHoldsValue());

        planetsCard.placeCargo(player1, 2, new Point(0, 2), 1);
        assertEquals(List.of(RED, RED, RED), player1.getCargoPool());
        assertEquals(4, player1.getCargoHoldsValue());

        planetsCard.confirmDone(player1);
        assertThrows(IllegalStateException.class, () -> planetsCard.placeCargo(player1, 2, new Point(0, 2), 1));
        assertThrows(IllegalStateException.class, () -> planetsCard.confirmDone(player3));

        planetsCard.placeCargo(player4, 1, new Point(1, 3), 1);
        assertEquals(List.of(BLUE), player4.getCargoPool());
        assertEquals(4, player4.getCargoHoldsValue());

        planetsCard.confirmDone(player4);

        assertEquals(4, player1.getCargoHoldsValue());
        assertEquals(0, player2.getCargoHoldsValue());
        assertEquals(0, player3.getCargoHoldsValue());
        assertEquals(4, player4.getCargoHoldsValue());

        assertTrue(planetsCard.isFinished(), "The card should be finished.");
    }

    @Test
    void AbandonedShip() {
        Player player1 = new Player("Player1", new int[]{0,0}, genericShipBoard(1));
        Player player2 = new Player("Player2", new int[]{0,0}, genericShipBoard(2));
        Player player3 = new Player("Player3", new int[]{0,0}, genericShipBoard(3));
        Player player4 = new Player("Player4", new int[]{0,0}, genericShipBoard(4));

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);

        GameModel gameModel = new GameModel(LEVEL_TWO, players);

        player1.setPosition(0);
        player2.setPosition(1);
        player3.setPosition(2);
        player4.setPosition(3);

        AbandonedShipCard abShipCard = new AbandonedShipCard(0,2, 6,3, 3);
        abShipCard.assignBoard(gameModel);

        assertEquals(6, player4.getCrewCount(), "Player4's crew count should be 6.");
        assertEquals(12, player3.getCrewCount(), "Player3's crew count should be 11.");
        assertEquals(8, player2.getCrewCount(), "Player2's crew count should be 8.");
        assertEquals(8, player1.getCrewCount(), "Player1's crew count should be 8.");

        abShipCard.activate();
        // The player order is player4, player3, player2, player1.
        assertThrows(IllegalStateException.class, () -> abShipCard.accept(player4), "Player4's turn should have been skipped.");
        assertThrows(IllegalStateException.class, () -> abShipCard.refuse(player4), "Player4's turn should have been skipped.");

        abShipCard.refuse(player3);

        assertEquals(1, player2.getPosition(), "Player2 should be at position 1.");
        assertEquals(0, player2.getCredits(), "Player2 should have 0 credits.");

        abShipCard.accept(player2);

        abShipCard.removeMemberFrom(player2, new Point(1,2), 0);
        assertThrows(IllegalArgumentException.class, () -> abShipCard.removeMemberFrom(player2, new Point(1,2), 0), "There should be no crew member at (1,2) index 0.");
        abShipCard.removeMemberFrom(player2, new Point(1,2), 1);
        abShipCard.removeMemberFrom(player2, new Point(1,3), 0);
        abShipCard.removeMemberFrom(player2, new Point(1,3), 1);
        abShipCard.removeMemberFrom(player2, new Point(3,2), 1);
        abShipCard.removeMemberFrom(player2, new Point(5,2), 1);
        assertThrows(IllegalStateException.class, () -> abShipCard.removeMemberFrom(player2, new Point(3,2), 0), "The player has already removed all the required members.");

        assertEquals(-3, player2.getPosition(), "Player2 should be at position -3.");
        assertEquals(3, player2.getCredits(), "Player2 should have gained 3 credits.");

        assertThrows(IllegalStateException.class, () -> abShipCard.refuse(player1), "Player2 already accepted the rewards.");

        assertTrue(abShipCard.isFinished(), "The card should be finished.");
    }

    @Test
    void AbandonedStation() {
        Player player1 = new Player("Player1", new int[]{0,0}, genericShipBoard(1));
        Player player2 = new Player("Player2", new int[]{0,0}, genericShipBoard(2));
        Player player3 = new Player("Player3", new int[]{0,0}, genericShipBoard(3));
        Player player4 = new Player("Player4", new int[]{0,0}, genericShipBoard(4));

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);

        GameModel gameModel = new GameModel(LEVEL_TWO, players);

        player1.setPosition(0);
        player2.setPosition(1);
        player3.setPosition(2);
        player4.setPosition(3);

        BlockType[] reward = {RED, GREEN, YELLOW, YELLOW};
        AbandonedStationCard abStationCard = new AbandonedStationCard(0,2, 8, reward, 3);
        abStationCard.assignBoard(gameModel);

        assertEquals(6, player4.getCrewCount(), "Player4's crew count should be 6.");
        assertEquals(12, player3.getCrewCount(), "Player3's crew count should be 11.");
        assertEquals(8, player2.getCrewCount(), "Player2's crew count should be 8.");
        assertEquals(8, player1.getCrewCount(), "Player1's crew count should be 8.");

        abStationCard.activate();
        // The player order is player4, player3, player2, player1.
        assertThrows(IllegalStateException.class, () -> abStationCard.accept(player4), "Player4's turn should have been skipped.");
        assertThrows(IllegalStateException.class, () -> abStationCard.refuse(player4), "Player4's turn should have been skipped.");

        abStationCard.refuse(player3);

        assertEquals(1, player2.getPosition(), "Player2 should be at position 1.");
        assertEquals(0, player2.getCargoHoldsValue(), "Player2 held value should be 0.");

        abStationCard.accept(player2);
        assertEquals(List.of(RED, GREEN, YELLOW, YELLOW), player2.getCargoPool());

        abStationCard.loadCargo(player2, 1, new Point(4, 1), 0);
        assertEquals(List.of(RED, YELLOW, YELLOW), player2.getCargoPool());
        assertEquals(2, player2.getCargoHoldsValue(), "Player2 held value should now be 2.");

        assertThrows(IllegalStateException.class, () -> abStationCard.loadCargo(player2, 0, new Point(4, 1), 1), "Shouldn't be able to place a red box in a normal container.");

        abStationCard.loadCargo(player2, 2, new Point(4, 3), 0);
        assertEquals(List.of(RED, YELLOW), player2.getCargoPool());
        assertEquals(5, player2.getCargoHoldsValue(), "Player2 held value should now be 5.");

        abStationCard.loadCargo(player2, 1, new Point(4, 1), 0);
        assertEquals(List.of(RED, GREEN), player2.getCargoPool());
        assertEquals(6, player2.getCargoHoldsValue(), "Player2 held value should now be 6.");

        abStationCard.confirmDone(player2);
        assertThrows(IllegalStateException.class, () -> abStationCard.loadCargo(player2, 1, new Point(4, 1), 0), "Player cannot place cargo anymore.");

        assertEquals(-3, player2.getPosition(), "Player2 should be at position -3.");
        assertEquals(6, player2.getCargoHoldsValue(), "Player2 held value should now be 6.");

        assertThrows(IllegalStateException.class, () -> abStationCard.refuse(player1), "Player2 already accepted the rewards.");

        assertTrue(abStationCard.isFinished(), "The card should be finished.");
    }
}