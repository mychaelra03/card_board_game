package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.Server.Model.cards.Card;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GameModel {
    private final BoardType boardType;
    private final List<Player> players;
    private final List<Card> deck;
    private final int flightTrackLength;

    public GameModel(BoardType boardType, List<Player> players) {
        this.boardType = boardType;
        //if (players.size() < 2 || players.size() > 4) throw new IllegalArgumentException("Invalid number of players.");
        this.players = players;

        this.deck = CardFactory.getInstance().getDeck(boardType);
        for(Card card : this.deck) {
            card.assignBoard(this);
        }
        this.flightTrackLength = (boardType == BoardType.LEARNING)? 18 : 24;
    }
    public GameModel(BoardType boardType) {
        this(boardType, new ArrayList<>());
    }

    public boolean addPlayer(Player player){
        if(players.size() < 4) {
            Integer prevNumPlayers = players.size();
            players.add(player);
            tellToListener(prevNumPlayers,(Integer)players.size());
            return true;
        }
        return false;
    }

    public List<Player> getSortedPlayers() {
        return players.stream()
                .filter(player -> !player.isEliminated())
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

    public static int rollTwoDice() {
        return (1 + (int)(Math.random() * 6)) + (1 + (int)(Math.random() * 6));
    }

    private void tellToListener(Integer from, Integer to) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this,"Player added",from,to );
        listener.propertyChange(evt);
    }

    private PropertyChangeListener listener;

    public void setListener(PropertyChangeListener listener) {
        this.listener = listener;
    }
}
