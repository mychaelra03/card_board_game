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
        this.players = players;
        this.deck = CardFactory.getInstance().generateDeck(boardType, this);
        this.flightTrackLength = boardType.getFlightTrackLength();
    }
    public GameModel(BoardType boardType) {
        this(boardType, new ArrayList<>());
    }

    public BoardType getBoardType() {
        return boardType;
    }
    public List<Card> getDeck() {
        return deck;
    }

    public boolean addPlayer(Player player){
        if(players.size() < 4) {
            Integer prevNumPlayers = players.size();
            players.add(player);
            tellToListener(prevNumPlayers, players.size());
            return true;
        }
        return false;
    }

    public boolean removePlayer(Player player) {
        Integer prevNumPlayers = players.size();
        boolean playerRemoved = players.remove(player);
        tellToListener(prevNumPlayers, players.size());
        return playerRemoved;
    }

    public List<Player> getSortedPlayers() {
        // Returns a sorted list of the non eliminated players.
        // First is the one furthest ahead (highest position value).
        return players.stream()
                .filter(player -> !player.isEliminated())
                .sorted(Comparator.comparingInt(Player::getPosition).reversed())
                .collect(Collectors.toList());
    }

    int mod(int x, int m) {
        // Had to add this method to correctly compare negative numbers.
        return ((x % m) + m) % m;
    }

    public enum Direction {
        FORWARD, BACKWARD
    }
    public void movePlayer(Player player, int steps, Direction direction) {
        int newPosition = player.getPosition();
        while(steps > 0) {
            newPosition += (direction == Direction.FORWARD) ? 1 : -1;
            final int thisPosition = newPosition;
            boolean occupied = players.stream().anyMatch(p -> mod(p.getPosition(), flightTrackLength) == mod(thisPosition, flightTrackLength));
            // Only decrease steps if the space is not occupied.
            if(!occupied) steps--;
        }
        player.setPosition(newPosition);
    }

    public void checkPlayersStatus() {
        if(players.isEmpty()) return;

        int highestPosition = getSortedPlayers().getFirst().getPosition();
        for(Player player : players) {
            if(player.isEliminated()) continue;

            boolean noCrew = player.getHumanCount() == 0;
            boolean lapped = player.getPosition() < highestPosition - flightTrackLength;
            // TODO: other conditions?

            if(noCrew || lapped) player.setEliminated(true);
        }
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
