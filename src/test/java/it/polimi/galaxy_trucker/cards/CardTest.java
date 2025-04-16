package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.BoardType;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Server.Model.cards.CombatZoneCard;
import it.polimi.galaxy_trucker.Server.Model.cards.PlanetsCard;
import it.polimi.galaxy_trucker.Server.Model.cards.StarDustCard;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.cards.projectile.CannonShot;
import it.polimi.galaxy_trucker.Server.Model.cards.projectile.Projectile;
import it.polimi.galaxy_trucker.Server.Model.tiles.*;
import it.polimi.galaxy_trucker.ShipBoard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.galaxy_trucker.BoardType.*;
import static it.polimi.galaxy_trucker.Server.Model.cards.CombatZoneCard.CombatRule.*;
import static it.polimi.galaxy_trucker.Server.Model.tiles.BlockType.*;
import static it.polimi.galaxy_trucker.Server.Model.tiles.Connector.*;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    public static final Connector[] FULL_UNIVERSAL_CONNECTORS = {UNIVERSAL, UNIVERSAL, UNIVERSAL, UNIVERSAL};

    private int getTileTypeCount(ShipBoard board) {
        Tile[][] tiles = board.getTiles();
        int count = 0;
        for(Tile[] row : tiles) {
            for(Tile tile : row) {
                if(!(tile instanceof EmptyTile)) count++;
            }
        }
        return count;
    }


    static private ShipBoard genericShipBoard(int id){

        //1st shipB
        ShipBoard shipBoard1 = new ShipBoard(LEVEL_TWO);
        shipBoard1.setTile(4,2,new CargoTile(0,new Connector[]{NONE,UNIVERSAL,NONE,UNIVERSAL}, CargoTile.CargoType.RED,1),0);
        shipBoard1.setTile(5,2,new CabinTile(0,new Connector[]{SINGLE,NONE,SINGLE,UNIVERSAL}),0);
        shipBoard1.setTile(6,2,new CargoTile(0,new Connector[]{NONE,NONE,SINGLE,NONE}, CargoTile.CargoType.NORMAL,3),0);
        shipBoard1.setTile(2,2,new CabinTile(0,new Connector[]{NONE,DOUBLE,UNIVERSAL,SINGLE}),0);
        shipBoard1.setTile(1,2,new AlienSupportTile(0,new Connector[]{SINGLE,SINGLE,NONE,SINGLE}, CrewType.BROWN_ALIEN),0);
        shipBoard1.setTile(0,2,new CargoTile(0,new Connector[]{NONE,SINGLE,NONE,NONE}, CargoTile.CargoType.RED,2),0);
        shipBoard1.setTile(1,1,new CannonTile(0, new Connector[]{NONE,DOUBLE,SINGLE,NONE},false),0);
        shipBoard1.setTile(2,1,new CargoTile(0,new Connector[]{SINGLE,SINGLE,NONE,DOUBLE}, CargoTile.CargoType.NORMAL,3),0);
        shipBoard1.setTile(3,1,new CargoTile(0,new Connector[]{SINGLE,SINGLE,SINGLE,UNIVERSAL}, CargoTile.CargoType.RED,1),0);
        shipBoard1.setTile(4,1,new CannonTile(0, new Connector[]{NONE,DOUBLE,NONE,UNIVERSAL},false),0);
        shipBoard1.setTile(5,1,new CannonTile(0, new Connector[]{NONE,NONE,SINGLE,UNIVERSAL},true),0);
        shipBoard1.setTile(2,0,new CannonTile(0, new Connector[]{NONE,NONE,SINGLE,NONE},false),0);
        shipBoard1.setTile(0,3,new BatteriesTile(0,new Connector[]{NONE,UNIVERSAL,NONE,NONE},2),0);
        shipBoard1.setTile(1,3,new ShieldTile(0,new Connector[]{NONE,DOUBLE,DOUBLE,DOUBLE}),0);
        shipBoard1.setTile(2,3, new StructuralTile(0, new Connector[]{DOUBLE,DOUBLE,UNIVERSAL,UNIVERSAL}),0);
        shipBoard1.setTile(3,3,new EngineTile(0,new Connector[]{SINGLE,DOUBLE,NONE,DOUBLE},false),0);
        shipBoard1.setTile(4,3, new StructuralTile(0, new Connector[]{NONE,SINGLE,UNIVERSAL,UNIVERSAL}),0);
        shipBoard1.setTile(5,3,new CabinTile(0,new Connector[]{SINGLE,UNIVERSAL,NONE,SINGLE}),0);
        shipBoard1.setTile(6,3,new BatteriesTile(0,new Connector[]{SINGLE,NONE,NONE,DOUBLE},3),0);
        shipBoard1.setTile(0,4,new CannonTile(0, new Connector[]{NONE,UNIVERSAL,NONE,NONE},false),0);
        shipBoard1.setTile(1,4,new CannonTile(0, new Connector[]{DOUBLE,DOUBLE,NONE,SINGLE},false),0);
        shipBoard1.setTile(2,4,new CannonTile(0, new Connector[]{UNIVERSAL,NONE,NONE,DOUBLE},true),0);
        shipBoard1.setTile(4,4,new CannonTile(0, new Connector[]{SINGLE,UNIVERSAL,NONE,NONE},true),0);
        shipBoard1.setTile(5,4,new CannonTile(0, new Connector[]{NONE,DOUBLE,NONE,UNIVERSAL},false),0);
        shipBoard1.setTile(6,4,new CannonTile(0, new Connector[]{NONE,NONE,NONE,UNIVERSAL},false),0);


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



        if(id == 1){
            return shipBoard1;
        }    if(id == 2){
            return shipBoard2;
        }    if(id == 3){
            return shipBoard3;
        }
        return shipBoard4;
    }


    @Test
    void testEpidemicCard() throws Exception {
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
        GameModel gameModel = new GameModel(LEVEL_TWO, players); //TODO: check for addPlayer if arraylist has already players in it


        gameModel.getSortedPlayers().forEach(player -> player.getShipBoard().applyEpidemicCard());

        assertEquals(2, player1.getCrewCount(),"Crew count should be: 2 ");
        assertEquals(3, player2.getCrewCount(),"Crew count should be: 3 ");
        assertEquals(4, player3.getCrewCount(),"Crew count should be: 4 ");
        assertEquals(6, player4.getCrewCount(),"Crew count should be: 6 ");
    }

    @Test
    void testStarDustCard() throws Exception {

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

        starDustCard.applyEffects(gameModel);

        assertEquals(2,player1.getPosition(),"Player1 (1st) should lose 6 position and be therefore be in position 2");
        assertEquals(-8,player2.getPosition(),"Player2 (4th) should lose 8 position and be therefore be in position -8");
        assertEquals(-2,player3.getPosition(),"Player3 (2nd) should lose 6 position and be therefore be in position -2");
        assertEquals(-11,player4.getPosition(),"Player4 (3rd) should lose 14 position and be therefore be in position -12");
    }

    @Test
    void testCombatZone() throws Exception {

        CannonShot[] cannonShots = new CannonShot[]{
                new CannonShot(Projectile.Size.SMALL, Projectile.Source.UP),
                new CannonShot(Projectile.Size.LARGE,Projectile.Source.UP),
                new CannonShot(Projectile.Size.SMALL,Projectile.Source.DOWN),};

        CombatZoneCard.CombatRule combatRule1 = new CombatZoneCard.CombatRule(Condition.CANNON_POWER,Punishment.FLIGHT_DAYS,3,null);

    //    CombatZoneCard.CombatRule combatRule2 = new CombatZoneCard.CombatRule(Condition.ENGINE_POWER,Punishment.CANNON_SHOTS,3,cannonShots);

        CombatZoneCard.CombatRule combatRule3 = new CombatZoneCard.CombatRule(Condition.CREW_SIZE,Punishment.CARGO_LOSS,3,null);

        CombatZoneCard.CombatRule combatRule4 = new CombatZoneCard.CombatRule(Condition.CANNON_POWER,Punishment.CREW_LOSS,3,null);

        CombatZoneCard.CombatRule[] combatRules = new CombatZoneCard.CombatRule[]{combatRule1 /*,combatRule2*/,combatRule3,combatRule4};

        CombatZoneCard combatZoneCard = new CombatZoneCard(0,2,combatRules);



        Player player1 = new Player("Player1", genericShipBoard(1));
        Player player2 = new Player("Player2", genericShipBoard(2));
        Player player3 = new Player("Player3", genericShipBoard(3));
        Player player4 = new Player("Player4", genericShipBoard(4));


        //((CannonTile) player1.getShipBoard().getTile(5,1)).setPowered(true);

        player1.getShipBoard().setCrewTypeAt(2,2,CrewType.BROWN_ALIEN);
        player2.getShipBoard().setCrewTypeAt(1,3,CrewType.BROWN_ALIEN);
        player3.getShipBoard().setCrewTypeAt(4,2,CrewType.BROWN_ALIEN);
        //player3.getShipBoard().setCrewTypeAt(6,3,CrewType.BROWN_ALIEN);
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


        assertEquals(6,player1.getPosition(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a 0");
        assertEquals(5,player2.getPosition(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a 0");
        assertEquals(4,player3.getPosition(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a 0");
        assertEquals(3,player4.getPosition(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a 0");

        assertEquals(1,player1.getShipBoard().getTotalHoldsValue(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(1,player2.getShipBoard().getTotalHoldsValue(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(1,player3.getShipBoard().getTotalHoldsValue(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(4,player4.getShipBoard().getTotalHoldsValue(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(12,player4.getShipBoard().getTotalBatteriesCount(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");;

        assertEquals(7,player1.getCrewCount(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(7,player2.getCrewCount(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(11,player3.getCrewCount(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(5,player4.getCrewCount(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");

        // apply the card
        combatZoneCard.applyEffects(gameModel);


        // rule 1

        assertEquals(6,player1.getPosition(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a 0");
        assertEquals(5,player2.getPosition(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a 0");
        assertEquals(0,player3.getPosition(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a 0");
        assertEquals(3,player4.getPosition(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a 0");
//
//        // rule 2
//
//
//        assertEquals(26,getTileTypeCount(player1.getShipBoard()));
//        assertEquals(25,getTileTypeCount(player2.getShipBoard()));
//        assertEquals(22,getTileTypeCount(player3.getShipBoard()));
//        assertEquals(22,getTileTypeCount(player4.getShipBoard()));


        // rule 3

        assertEquals(1,player1.getShipBoard().getTotalHoldsValue(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(1,player2.getShipBoard().getTotalHoldsValue(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(1,player3.getShipBoard().getTotalHoldsValue(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(0,player4.getShipBoard().getTotalHoldsValue(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(11,player4.getShipBoard().getTotalBatteriesCount(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");


        // rule 4

        assertEquals(7,player1.getCrewCount(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(7,player2.getCrewCount(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(8,player3.getCrewCount(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");
        assertEquals(5,player4.getCrewCount(),"Per la prima combatRule il player 3 dovrebbe tornare indietro di 3 posizioni fino  a -3");

    }


//    @Test
//    void testPlanetsCard(){
//
//
//        Player player1 = new Player("Player1", genericShipBoard(1));
//        Player player2 = new Player("Player2", genericShipBoard(2));
//        Player player3 = new Player("Player3", genericShipBoard(3));
//        Player player4 = new Player("Player4", genericShipBoard(4));
//
//
//        List<Player> players = new ArrayList<>();
//
//        players.add(player1);
//        players.add(player2);
//        players.add(player3);
//        players.add(player4);
//
//        GameModel gameModel = new GameModel(2,players);
//
//        player1.setPosition(4);
//        player2.setPosition(3);
//        player3.setPosition(2);
//        player4.setPosition(1);
//
//
//        BlockType[][] blockTypes = {
//                {RED, RED, RED, RED},
//                {GREEN, GREEN, YELLOW, YELLOW},
//                {GREEN, GREEN},
//                {BLUE, BLUE}
//        };
//        PlanetsCard planetsCard = new PlanetsCard(0,2,blockTypes,3);
//
//        planetsCard.applyEffects(gameModel);
//
//        assertEquals(16,player1.getShipBoard().getTotalHoldsValue(),"total value of 16 . 4$ * 4");
//        assertEquals(10,player2.getShipBoard().getTotalHoldsValue(),"total value of 10 . (3$ * 2)+(2$ * 2)");
//        assertEquals(4,player3.getShipBoard().getTotalHoldsValue(),"total value of 4 . 2$ * 2");
//        assertEquals(2,player4.getShipBoard().getTotalHoldsValue(),"total value of 2 . 1$ * 2");
//
//
//    }



}