package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.Server.Model.cards.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameModelTest {

    @Test
    void testCardFactory() throws Exception {
        CardFactory cardFactory = CardFactory.getInstance();
        List<Card> levelTwoDeck = cardFactory.getLevelTwoDeck();
        List<Card> levelTwoDeck2 = cardFactory.getLevelTwoDeck();
        List<Card> learningDeck = cardFactory.getLearningDeck();
        List<Card> learningDeck2 = cardFactory.getLearningDeck();
        System.out.println();

    }

    @Test
    void addPlayer() {
    }

    @Test
    void getSortedPlayers() {
    }

    @Test
    void movePlayerBackward() throws Exception {
        Player player1 = new Player("TestPlayer1");
        Player player2 = new Player("TestPlayer2");
        Player player3 = new Player("TestPlayer3");
        Player player4 = new Player("TestPlayer4");

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

        gameModel.movePlayer(player2,8, GameModel.Direction.BACKWARD);


        assertEquals(-7,player2.getPosition(),"Player2 should move backward of 8 position so be in -7 position");



    }
}