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
    void movePlayerBackward() {
    }
}