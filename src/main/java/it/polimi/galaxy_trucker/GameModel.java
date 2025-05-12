package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.lobby.BoardSelectMessage;
import it.polimi.galaxy_trucker.common.messages.login.LobbyJoinResponse;
import it.polimi.galaxy_trucker.common.messages.PlayerInfo;
import it.polimi.galaxy_trucker.server.events.*;
import it.polimi.galaxy_trucker.server.events.shipBuilding.CardsReturnedEvent;
import it.polimi.galaxy_trucker.server.events.shipBuilding.CardsTakenEvent;
import it.polimi.galaxy_trucker.server.events.shipBuilding.TileReturnedEvent;
import it.polimi.galaxy_trucker.server.events.shipBuilding.TileTakenEvent;
import it.polimi.galaxy_trucker.server.model.cards.Card;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.galaxy_trucker.GameModel.GameState.*;
import static it.polimi.galaxy_trucker.Player.PlayerState.*;

public class GameModel {
    private BoardType boardType;
    private final List<Player> players;
    private List<Card> deck;
    private List<List<Card>> cardPiles;

    private final Set<Integer> availableTiles;
    private final Set<Integer> availableCardPiles;

    public enum GameState {
        LOBBY,
        BUILDING_SHIPS,
        FLIGHT_PHASE,
        GAME_OVER
    }
    private GameState gameState = LOBBY;

    public GameModel() {
        this.boardType = BoardType.LEVEL_TWO;
        this.players = new ArrayList<>();
        this.availableTiles = new HashSet<>();
        for (int i = 0; i < 152; i++) {
            availableTiles.add(i);
        }
        this.availableCardPiles = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            availableCardPiles.add(i);
        }
        this.deck = new ArrayList<>();
    }

    public BoardType getBoardType() {
        return boardType;
    }
    public void setBoardType(BoardType boardType) {
        this.boardType = boardType;
    }
    public void cycleBoard(BoardSelectMessage.Action action) {
        BoardType[] cycleTypes = BoardType.values();

        int currentIndex = Arrays.asList(cycleTypes).indexOf(boardType);

        int newIndex = switch (action) {
            case NEXT -> (currentIndex + 1) % cycleTypes.length;
            case PREVIOUS -> (currentIndex - 1 + cycleTypes.length) % cycleTypes.length;
        };

        this.boardType = cycleTypes[newIndex];

        eventDispatcher.fireEvent(new BoardTypeUpdateEvent(boardType));
    }

    public void generateDeck() {
        this.deck = CardFactory.getInstance().generateDeck(boardType);
        splitDeckIntoPiles();
    }
    public List<Card> getDeck() {
        return deck;
    }
    public List<Card> getPile(int pileId) {
        if(pileId < 0 || pileId >= cardPiles.size()) throw new IllegalArgumentException("pileId must be between 0 and " + (cardPiles.size() - 1));
        return cardPiles.get(pileId);
    }

    public LobbyJoinResponse.JoinStatus canAddPlayer(Player player) {
        if(gameState != LOBBY) return LobbyJoinResponse.JoinStatus.GAME_STARTED;
        if(players.contains(player)) throw new IllegalArgumentException("Player already in the game.");
        if(players.size() >= 4) return LobbyJoinResponse.JoinStatus.LOBBY_FULL;

        boolean nameTaken = players.stream().anyMatch(p -> p.getName().equalsIgnoreCase(player.getName()));
        if(nameTaken) return LobbyJoinResponse.JoinStatus.NAME_TAKEN;

        return LobbyJoinResponse.JoinStatus.SUCCESS;
    }


    public void addPlayer(Player player) {
        verifyGameState(LOBBY);
        if(players.contains(player)) throw new IllegalArgumentException("Player already in the game.");
        LobbyJoinResponse.JoinStatus canAddPlayer = canAddPlayer(player);
        if(canAddPlayer != LobbyJoinResponse.JoinStatus.SUCCESS) throw new IllegalStateException("Can't add player :" + canAddPlayer);

        players.add(player);
        assignFirstAvailableColor(player);
        if (players.size() == 1) player.setAdmin(true);

        eventDispatcher.fireEvent(new PlayersUpdateEvent(getPlayersInfo(), player.getName()));
    }

    public void removePlayer(String username) {

        for (Player p : players) {
            if (p.getName().equals(username)) {
                removePlayer(p);
                return;
            }
        }
        throw new IllegalArgumentException("Player " + username + " not found.");
    }

    public void removePlayer(Player player) {
        if(!players.contains(player)) throw new IllegalArgumentException("Player " + player.getName() + " not found.");

        players.remove(player);

        if(player.isAdmin() && !players.isEmpty()) players.getFirst().setAdmin(true);

        if (!players.isEmpty()) {
            if (gameState != GameState.LOBBY) {
                Logger.logInfo("A player left during the game. Returning to lobby...");
                eventDispatcher.fireEvent(new GameResetEvent(player.getName()));
                resetToLobby();
            }
            eventDispatcher.fireEvent(new PlayersUpdateEvent(getPlayersInfo(), player.getName()));
        } else {
            Logger.logInfo("All players have left the game. Resetting game state.");
            resetCompletely();
        }
    }

    private void assignFirstAvailableColor(Player player) {
        Set<Player.Color> takenColors = new HashSet<>();
        for(Player p : players) {
            takenColors.add(p.getColor());
        }

        for(Player.Color color : Player.Color.values()) {
            if(!takenColors.contains(color)) {
                player.setColor(color);
                return;
            }
        }
        throw new IllegalStateException("No available colors.");
    }

    public void setNewAdmin(String username) {
        for (Player p : players) {
            p.setAdmin(username.equals(p.getName()));
        }
        eventDispatcher.fireEvent(new PlayersUpdateEvent(getPlayersInfo(), username));
    }

    public void updatePlayersReady() {
        int playersReady = 0;
        for(Player p : players) {
            if(p.isReady()) playersReady++;
        }
        if(players.size() > 1 && playersReady == players.size()) {
            setGameState(BUILDING_SHIPS);
        } else {
            eventDispatcher.fireEvent(new PlayersUpdateEvent(getPlayersInfo(), ""));
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
    public Card getCurrentCard() {
        return deck.getFirst();
    }
    public List<Player> getPlayers() {
        return players;
    }


    public boolean TryTakeTile(String playerName, int id) {
        synchronized (availableTiles) {
            if (!availableTiles.contains(id)) return false;
            availableTiles.remove(id);
            eventDispatcher.fireEvent(new TileTakenEvent(playerName, id));
            return true;
        }
    }
    public void returnTile(int id) {
        if(id < 0 || id >= 152) {
            Logger.logError("Tile id " + id + " is out of range.");
            return;
        }
        synchronized (availableTiles) {
            if (availableTiles.contains(id)) throw new IllegalStateException("Tile " + id + " was already on the board.");
            availableTiles.add(id);
            eventDispatcher.fireEvent(new TileReturnedEvent(id));
        }
    }
    public boolean tryTakeCardPile(String playerName, int id) {
        synchronized (availableCardPiles) {
            if (!availableCardPiles.contains(id)) return false;
            availableCardPiles.remove(id);
            eventDispatcher.fireEvent(new CardsTakenEvent(playerName, id));
            return true;
        }
    }
    public void returnCardPile(int id) {
        if(id < 0 || id >= 3) {
            Logger.logError("Card pile id " + id + " is out of range.");
            return;
        }
        synchronized (availableCardPiles) {
            if (availableCardPiles.contains(id)) throw new IllegalStateException("Tile " + id + " was already on the board.");
            availableCardPiles.add(id);
            eventDispatcher.fireEvent(new CardsReturnedEvent(id));
        }
    }
    public void splitDeckIntoPiles() {
        if (deck.size() % 4 != 0) {
            throw new IllegalArgumentException("Deck size must be divisible by 4");
        }
        this.cardPiles = List.of(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        int pileSize = deck.size() / 4;

        for (int i = 0; i < pileSize; i++) {
            for (int pile = 0; pile < 4; pile++) {
                int index = i * 4 + pile;
                cardPiles.get(pile).add(deck.get(index));
            }
        }
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

    private void setGameState(GameState state) {
        this.gameState = state;
        switch(state) {
            case BUILDING_SHIPS -> {
                generateDeck();
                players.forEach(player -> {
                    player.setState(BUILDING_SHIP);
                    player.setNewShipBoard(boardType);
                });
            }
        }
        eventDispatcher.fireEvent(new GameStateUpdateEvent(state));
    }
    public GameState getGameState() {
        return gameState;
    }
    private void verifyGameState(GameState expectedState) {
        if(gameState != expectedState) {
            throw new IllegalStateException("Expected state: " + expectedState + ". Current state: " + gameState + ".");
        }
    }

    private void resetToLobby() {
        this.gameState = GameState.LOBBY;
        for (Player player : players) player.setState(IDLE);
        this.deck.clear();
        this.cardPiles = null;
        this.availableTiles.clear();
        for (int i = 0; i < 152; i++) availableTiles.add(i);
        this.availableCardPiles.clear();
        for (int i = 0; i < 3; i++) availableCardPiles.add(i);
    }

    public void resetCompletely() {
        resetToLobby();
        this.players.clear();
        this.boardType = BoardType.LEVEL_TWO;
    }

    private List<PlayerInfo> getPlayersInfo(){
        return players.stream()
                .map(p -> new PlayerInfo(p.getName(),p.getProfilePictureIds(),p.getColor().toString(),p.isAdmin(),p.isReady()))
                .collect(Collectors.toList());
    }
    private PlayerInfo getPlayerInfo(Player player) {
        return new PlayerInfo(player.getName(),player.getProfilePictureIds(),player.getColor().toString(),player.isAdmin(),player.isReady());
    }

    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public <T extends EventModel> void addEventListener(Class<T> eventType, ModelEventListener<T> listener) {
        eventDispatcher.addListener(eventType, listener);
    }
}
