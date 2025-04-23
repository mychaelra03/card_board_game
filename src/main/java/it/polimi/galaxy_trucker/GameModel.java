package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.Server.Model.cards.Card;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static it.polimi.galaxy_trucker.GameModel.GameState.*;
import static it.polimi.galaxy_trucker.Player.PlayerState.*;

public class GameModel {
    private final BoardType boardType;
    private final List<Player> players;
    private final List<Card> deck;

    public enum GameState {
        WAITING_FOR_PLAYERS,
        BUILDING_SHIPS,
        APPLYING_CARDS,
        GAME_OVER
    }
    private GameState gameState = WAITING_FOR_PLAYERS;

    public GameModel(BoardType boardType) {
        this.boardType = boardType;
        this.players = new ArrayList<>();
        this.deck = CardFactory.getInstance().generateDeck(boardType);
    }

    public BoardType getBoardType() {
        return boardType;
    }
    public List<Card> getDeck() {
        return deck;
    }

    public boolean addPlayer(Player player) {
        verifyGameState(WAITING_FOR_PLAYERS);
        if(players.contains(player)) throw new IllegalArgumentException("Player already in the game.");
        if (players.size() >= 4) return false;

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
        for(int i = 0; i < players.size(); i++) {
            assignColor(players.get(i), i);
        }
    }

    public List<Player> getSortedPlayers() {
        // Returns a sorted list of the non eliminated players.
        // First is the one furthest ahead (highest position value).
        return players.stream()
                .filter(player -> player.getState() != ELIMINATED)
                .sorted(Comparator.comparingInt(Player::getPosition).reversed())
                .collect(Collectors.toList());
    }

    private int mod(int x, int m) {
        // Had to add this method to correctly compare negative numbers.
        return ((x % m) + m) % m;
    }
    private boolean isPositionOccupied(Player movingPlayer, int position) {
        int flightTrackLength = boardType.getFlightTrackLength();
        return players.stream()
                .filter(p -> p != movingPlayer)
                .anyMatch(p -> mod(p.getPosition(), flightTrackLength) == mod(position, flightTrackLength));
    }

    public void setPlayerPosition(Player player, int position) {
        if(player.getState() == ELIMINATED) throw new IllegalStateException("Player " + player.getName() + " is eliminated.");
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
            if(player.getState() == ELIMINATED) continue;

            boolean noCrew = player.getHumanCount() == 0;
            boolean lapped = player.getPosition() < highestPosition - boardType.getFlightTrackLength();
            // TODO: other conditions?

            if(noCrew || lapped) player.setState(ELIMINATED);
        }
    }

    protected final void setGameState(GameState state) {
        this.gameState = state;
    }
    protected final void verifyGameState(GameState expectedState) {
        if(gameState != expectedState) {
            throw new IllegalStateException("Expected state: " + expectedState + ". Current state: " + gameState + ".");
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
