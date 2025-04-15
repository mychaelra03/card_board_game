package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.BoardType;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Server.Model.cards.EpidemicCard;
import it.polimi.galaxy_trucker.Server.Model.cards.StarDustCard;
import it.polimi.galaxy_trucker.Player;
import it.polimi.galaxy_trucker.Server.Model.tiles.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.galaxy_trucker.BoardType.*;
import static it.polimi.galaxy_trucker.Server.Model.tiles.Connector.*;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    public static final Connector[] FULL_UNIVERSAL_CONNECTORS = {UNIVERSAL, UNIVERSAL, UNIVERSAL, UNIVERSAL};

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
        GameModel gameModel = new GameModel(LEVEL_TWO, players); //TODO: check for addPlayer if arraylist has already players in it

        EpidemicCard epidemicCard = new EpidemicCard(24,1);
        epidemicCard.assignBoard(gameModel);
        epidemicCard.activate();

        assertEquals(2, player1.getCrewCount(),"Crew count should be: 2 ");
        assertEquals(3, player2.getCrewCount(),"Crew count should be: 3 ");
        assertEquals(4, player3.getCrewCount(),"Crew count should be: 4 ");
        assertEquals(6, player4.getCrewCount(),"Crew count should be: 6 ");
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

        assertEquals(2,player1.getPosition(),"Player1 (1st) should lose 6 position and be therefore be in position 2");
        assertEquals(-8,player2.getPosition(),"Player2 (4th) should lose 8 position and be therefore be in position -8");
        assertEquals(-2,player3.getPosition(),"Player3 (2nd) should lose 6 position and be therefore be in position -2");
        assertEquals(-11,player4.getPosition(),"Player4 (3rd) should lose 14 position and be therefore be in position -12");
    }

    @Test
    void openSpaceCard() {
        Player player1 = new Player("TestPlayer1", new int[]{0, 0});
        Player player2 = new Player("TestPlayer2", new int[]{0, 0});
        Player player3 = new Player("TestPlayer3", new int[]{0, 0});
        Player player4 = new Player("TestPlayer4", new int[]{0, 0});
        player1.setNewShipBoard(LEVEL_TWO);
        player2.setNewShipBoard(LEVEL_TWO);
        player3.setNewShipBoard(LEVEL_TWO);
        player4.setNewShipBoard(LEVEL_TWO);

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


    }
}