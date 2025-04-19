package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.Server.Model.cards.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.galaxy_trucker.BoardType.*;
import static org.junit.jupiter.api.Assertions.*;

class GameModelTest {

    @Test
    void addPlayer() {
    }

    @Test
    void getSortedPlayers() {
    }

    @Test
    void movePlayerBackward() throws Exception {
        Player player1 = new Player("TestPlayer1", new int[]{0, 0});
        Player player2 = new Player("TestPlayer2", new int[]{0, 0});
        Player player3 = new Player("TestPlayer3", new int[]{0, 0});
        Player player4 = new Player("TestPlayer4", new int[]{0, 0});

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

        gameModel.movePlayer(player2,8, GameModel.Direction.BACKWARD);


        assertEquals(-7,player2.getPosition(),"Player2 should move backward of 8 position so be in -7 position");

    }
}