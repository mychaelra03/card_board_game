package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.cards.Card;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GameBoard {
    private int boardType;
    private List<Player> players;
    private List<Card> deck;
    private int flightTrackLength;

    public GameBoard(int boardType, List<Player> players) throws Exception {
        if (boardType < 1 || boardType > 2) throw new IllegalArgumentException("boardType must be between 1 and 2.");
        this.boardType = boardType;
        if (players.size() < 2 || players.size() > 4) throw new IllegalArgumentException("Invalid number of players.");
        this.players = players;

        CardFactory cardFactory = CardFactory.getInstance();
        this.deck = (boardType == 1) ? cardFactory.getLearningDeck() : cardFactory.getLevelTwoDeck();
        this.flightTrackLength = (boardType == 1)? 18 : 24;
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    public List<Player> getSortedPlayers() {
        return players.stream()
                .sorted(Comparator.comparingInt(Player::getPosition))
                .collect(Collectors.toList());
    }

    public enum Direction {
        FORWARD, BACKWARD
    }
    public void movePlayer(Player player, int steps, Direction direction) {
        int newPosition = player.getPosition();
        while(steps > 0) {
            newPosition += (direction == Direction.FORWARD) ? 1 : -1;
            final int thisPosition = newPosition;
            boolean occupied = players.stream().anyMatch(p -> p.getPosition() == thisPosition);
            // Only decrease steps if the space is not occupied.
            if(!occupied) steps--;
        }
        player.setPosition(newPosition);
    }

}
