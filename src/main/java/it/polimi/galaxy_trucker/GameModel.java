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

    private static final int MAX_PLAYERS = 4;

    public GameModel(BoardType boardType) {
        this.boardType = boardType;
        this.players = new ArrayList<>();
        this.deck = CardFactory.getInstance().generateDeck(boardType);
        this.flightTrackLength = boardType.getFlightTrackLength();
    }

    public BoardType getBoardType() {
        return boardType;
    }
    public List<Card> getDeck() {
        return deck;
    }

    public boolean addPlayer(Player player){
        if(players.contains(player)) throw new IllegalArgumentException("Player already in the game.");
        if (players.size() >= MAX_PLAYERS) return false;

        int prevSize = players.size();
        players.add(player);
        assignColor(player, players.size() - 1);
        tellToListener(prevSize, players.size());
        return true;
    }

    public void removePlayer(Player player) {
        int prevSize = players.size();
        if(!players.remove(player)) throw new IllegalArgumentException("Player is not in this game.");
        updatePlayersColor();
        tellToListener(prevSize, players.size());
    }

    private void assignColor(Player player, int index) {
        Player.Color[] colors = Player.Color.values();
        if(index < 0 || index >= colors.length) throw new IllegalArgumentException("Invalid color index: " + index);
        player.setColor(colors[index]);
    }

    private void updatePlayersColor() {
        for (int i = 0; i < players.size(); i++) {
            assignColor(players.get(i), i);
        }
    }

    public List<Player> getSortedPlayers() {
        // Returns a sorted list of the non eliminated players.
        // First is the one furthest ahead (highest position value).
        return players.stream()
                .filter(player -> !player.isEliminated())
                .sorted(Comparator.comparingInt(Player::getPosition).reversed())
                .collect(Collectors.toList());
    }

    private int mod(int x, int m) {
        // Had to add this method to correctly compare negative numbers.
        return ((x % m) + m) % m;
    }
    private boolean isPositionOccupied(Player movingPlayer, int position) {
        return players.stream()
                .filter(p -> p != movingPlayer)
                .anyMatch(p -> mod(p.getPosition(), flightTrackLength) == mod(position, flightTrackLength));
    }

    public void setPlayerPosition(Player player, int position) {
        if(player.isEliminated()) throw new IllegalStateException("Player " + player.getName() + " is eliminated.");
        if(isPositionOccupied(player, position)) throw new IllegalArgumentException("Position is occupied");
        player.setPosition(position);
    }

    private void movePlayer(Player player, int steps, boolean forward) {
        int newPosition = player.getPosition();
        while(steps > 0) {
            newPosition += forward? 1 : -1;
            final int thisPosition = newPosition;
            // Only decrease steps if the space is not occupied.
            if(!isPositionOccupied(player, thisPosition)) steps--;
        }
        setPlayerPosition(player, newPosition);
    }
    public void movePlayerForward(Player player, int steps) {
        movePlayer(player, steps, true);
    }
    public void movePlayerBackward(Player player, int steps) {
        movePlayer(player, steps, false);
    }

    public void updatePlayersStatus() {
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
        if(listener != null) {
            PropertyChangeEvent evt = new PropertyChangeEvent(this, "Player added", from, to);
            listener.propertyChange(evt);
        }
    }

    private PropertyChangeListener listener;

    public void setListener(PropertyChangeListener listener) {
        this.listener = listener;
    }
}
