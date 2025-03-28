package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Model.cards.StarDustCard;
import it.polimi.galaxy_trucker.Model.tiles.*;
import it.polimi.galaxy_trucker.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.galaxy_trucker.Model.tiles.Connector.*;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    public static final Connector[] FULL_UNIVERSAL_CONNECTORS = {UNIVERSAL, UNIVERSAL, UNIVERSAL, UNIVERSAL};

    @Test
    void testEpidemicCard() throws Exception {
        Player player1 = new Player("TestPlayer1", Player.Color.BLUE,2);
        Player player2 = new Player("TestPlayer2", Player.Color.YELLOW,2);
        Player player3 = new Player("TestPlayer3", Player.Color.RED,2);
        Player player4 = new Player("TestPlayer4", Player.Color.GREEN,2);
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
        GameModel gameModel = new GameModel(2, players); //TODO: check for addPlayer if arraylist has already players in it


        gameModel.getSortedPlayers().forEach(player -> player.getShipBoard().applyEpidemicCard());

        assertEquals(2, player1.getCrewCount(),"Crew count should be: 2 ");
        assertEquals(3, player2.getCrewCount(),"Crew count should be: 3 ");
        assertEquals(4, player3.getCrewCount(),"Crew count should be: 4 ");
        assertEquals(6, player4.getCrewCount(),"Crew count should be: 6 ");
    }

    @Test
    void testStarDustCard() throws Exception {

        Player player1 = new Player("TestPlayer1", Player.Color.BLUE,2);
        Player player2 = new Player("TestPlayer2", Player.Color.YELLOW,2);
        Player player3 = new Player("TestPlayer3", Player.Color.RED,2);
        Player player4 = new Player("TestPlayer4", Player.Color.GREEN,2);

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

        GameModel gameModel = new GameModel(2, players);

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



}