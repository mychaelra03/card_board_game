package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.cards.Card;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {

    @Test
    void testCardFactory() throws Exception {
        CardFactory cardFactory = CardFactory.getInstance();
        List<Card> level2Deck = cardFactory.getLevelTwoDeck();
    }

    @Test
    void addPlayer() {
    }

    @Test
    void getSortedPlayers() {
    }

    @Test
    void movePlayerBackward() {
    }
}