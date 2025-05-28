package it.polimi.galaxy_trucker.server.model;

import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.endGame.PlayerScores;
import it.polimi.galaxy_trucker.common.messages.lobby.BoardSelectMessage;
import it.polimi.galaxy_trucker.common.messages.login.JoinResponseMessage;
import it.polimi.galaxy_trucker.common.messages.PlayerInfo;
import it.polimi.galaxy_trucker.server.events.*;
import it.polimi.galaxy_trucker.server.events.flight.*;
import it.polimi.galaxy_trucker.server.events.flight.cards.CardPickedEvent;
import it.polimi.galaxy_trucker.server.events.gameOver.GameOverEvent;
import it.polimi.galaxy_trucker.server.events.lobby.BoardTypeUpdateEvent;
import it.polimi.galaxy_trucker.server.events.lobby.PlayersUpdateEvent;
import it.polimi.galaxy_trucker.server.events.shipBuilding.*;
import it.polimi.galaxy_trucker.server.model.cards.Card;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static it.polimi.galaxy_trucker.server.model.GameModel.GameState.*;
import static it.polimi.galaxy_trucker.server.model.Player.PlayerState.*;

public class GameModel {
    private BoardType boardType;
    private final List<Player> players;
    private List<Card> deck;
    private List<List<Card>> cardPiles;

    private final EventDispatcher eventDispatcher = new EventDispatcher();

    private final Set<Integer> availableTiles;
    private final Set<Integer> availableCardPiles;
    private final Set<Integer> availablePositions;

    private final Set<Player> invalidShips = new HashSet<>();
    private final Set<Player> choosingCrewType = new HashSet<>();

    private Card currentCard;

    private final BuildingTimerHandler timerHandler = new BuildingTimerHandler(80, eventDispatcher, this::onTimerFinished);
    private Boolean timerFlipAvailable = false;

    public enum GameState {
        LOBBY,
        BUILDING_SHIPS,
        CORRECTING_SHIP,
        ASSIGNING_CREW,
        PICKING_CARDS,
        WAITING
    }
    private GameState gameState = LOBBY;

    public GameModel() {
        this.players = new ArrayList<>();
        this.boardType = BoardType.LEVEL_TWO;
        this.availableTiles = new HashSet<>();
        for (int i = 0; i < 152; i++) {
            availableTiles.add(i);
        }
        this.availableCardPiles = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            availableCardPiles.add(i);
        }
        this.availablePositions = new HashSet<>();
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
    public void shuffleDeck() {
        Collections.shuffle(deck);

        if (boardType == BoardType.LEVEL_TWO && !deck.isEmpty()) {
            if (deck.getFirst().getLevel() != 2) {
                for (int i = 1; i < deck.size(); i++) {
                    if (deck.get(i).getLevel() == 2) {
                        Collections.swap(deck, 0, i);
                        break;
                    }
                }
            }
        }
        this.cardPiles = null;
    }
    public List<Card> getDeck() {
        return deck;
    }
    public List<Card> getPile(int pileId) {
        if(pileId < 0 || pileId >= cardPiles.size()) throw new IllegalArgumentException("pileId must be between 0 and " + (cardPiles.size() - 1));
        return cardPiles.get(pileId);
    }

    public JoinResponseMessage.JoinStatus canAddPlayer(Player player) {
        if(gameState != LOBBY) return JoinResponseMessage.JoinStatus.GAME_STARTED;
        if(players.contains(player)) throw new IllegalArgumentException("Player already in the game.");
        if(players.size() >= 4) return JoinResponseMessage.JoinStatus.LOBBY_FULL;

        boolean nameTaken = players.stream().anyMatch(p -> p.getName().equalsIgnoreCase(player.getName()));
        if(nameTaken) return JoinResponseMessage.JoinStatus.NAME_TAKEN;

        return JoinResponseMessage.JoinStatus.SUCCESS;
    }


    public void addPlayer(Player player) {
        if(players.contains(player)) throw new IllegalArgumentException("Player already in the game.");
        JoinResponseMessage.JoinStatus canAddPlayer = canAddPlayer(player);
        if(canAddPlayer != JoinResponseMessage.JoinStatus.SUCCESS) throw new IllegalStateException("Can't add player :" + canAddPlayer);

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
            setGameStateAfterWait(BUILDING_SHIPS);
        } else {
            eventDispatcher.fireEvent(new PlayersUpdateEvent(getPlayersInfo(), null));
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
    public Player getFirstPlayer() {
        return getSortedPlayers().getFirst();
    }

    private int mod(int x, int m) {
        // Had to add this method to correctly compare negative numbers.
        return ((x % m) + m) % m;
    }
    private boolean isPositionOccupied(Player movingPlayer, int position) {
        int flightTrackLength = boardType.getFlightTrackLength();
        return getSortedPlayers().stream()
                .filter(p -> p != movingPlayer)
                .anyMatch(p -> mod(p.getPosition(), flightTrackLength) == mod(position, flightTrackLength));
    }
    public Card getCurrentCard() {
        return currentCard;
    }


    public boolean TryTakeTile(Player player, int id) {
        if(player.getState() != BUILDING_SHIP) return false;

        synchronized (availableTiles) {
            if (!availableTiles.contains(id)) return false;
            availableTiles.remove(id);
            eventDispatcher.fireEvent(new TileTakenEvent(player.getName(), id));
            return true;
        }
    }
    public Integer TakeRandomTile(Player player) {
        if(player.getState() != BUILDING_SHIP) return null;

        synchronized (availableTiles) {
            if (availableTiles.isEmpty()) return null;

            List<Integer> tileList = new ArrayList<>(availableTiles);
            int randomIndex = new Random().nextInt(tileList.size());
            int id = tileList.get(randomIndex);

            availableTiles.remove(id);
            eventDispatcher.fireEvent(new TileTakenEvent(player.getName(), id));
            return id;
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
    public boolean tryTakeCardPile(Player player, int id) {
        if(player.getState() != BUILDING_SHIP) return false;

        synchronized (availableCardPiles) {
            if (!availableCardPiles.contains(id)) return false;
            availableCardPiles.remove(id);
            eventDispatcher.fireEvent(new CardsTakenEvent(player.getName(), id));
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

        eventDispatcher.fireEvent(new PlayerPositionUpdateEvent(player.getName(), newPosition));
    }
    public void movePlayerForward(Player player, int steps) {
        movePlayer(player, steps, true);
    }
    public void movePlayerBackward(Player player, int steps) {
        movePlayer(player, steps, false);
    }

    public PlayerScores getPlayerScores(Player player, Set<Player> bestShipPlayers) {
        boolean isEliminated = player.getState() == ELIMINATED;
        boolean hasBestShip = bestShipPlayers.contains(player);

        int positionScore = isEliminated? 0 : boardType.getPositionReward(getSortedPlayers().indexOf(player) + 1);
        int bestShipScore = (isEliminated || !hasBestShip) ? 0 : boardType.getBestShipReward();
        int cargoScore = isEliminated? (player.getTotalHoldsValue() + 1) / 2 : player.getTotalHoldsValue();
        int lostTilesScore = -player.getRemovedTilesCount();

        int totaleScore = positionScore + bestShipScore + cargoScore + lostTilesScore;

        return new PlayerScores(player.getName(), positionScore, bestShipScore, cargoScore, lostTilesScore, totaleScore);
    }

    public Set<Player> getPlayersWithBestShip() {
        Set<Player> playersWithBestShip = new HashSet<>();
        int minExposed = Integer.MAX_VALUE;

        for (Player player : players) {
            if(player.getState() == ELIMINATED) continue;
            int exposed = player.getExposedConnectorsCount();

            if (exposed < minExposed) {
                minExposed = exposed;
                playersWithBestShip.clear();
                playersWithBestShip.add(player);
            } else if (exposed == minExposed) {
                playersWithBestShip.add(player);
            }
        }

        return playersWithBestShip;
    }

    public void updatePlayersStatus() {
        if(getSortedPlayers().isEmpty()) return;

        int highestPosition = getSortedPlayers().getFirst().getPosition();
        for(Player player : players) {
            if(player.getState() == ELIMINATED) continue;

            boolean givingUp = player.getState() == GIVING_UP;
            boolean noCrew = player.getHumanCount() == 0;
            boolean lapped = player.getPosition() < highestPosition - boardType.getFlightTrackLength();

            if(givingUp || noCrew || lapped) player.setState(ELIMINATED);
        }
    }

    private void setGameState(GameState state) {
        GameState previousGameState = gameState;
        this.gameState = state;

        eventDispatcher.fireEvent(new GameStateUpdateEvent(previousGameState, state));

        handleNewGameState(state);
    }

    private void handleNewGameState(GameState gameState) {
        switch(gameState) {
            case BUILDING_SHIPS -> {
                timerFlipAvailable = true;
                flipTimer(false);
            }
            case CORRECTING_SHIP -> {
                players.forEach(Player::applyReservedTilePenalty);

                invalidShips.clear();
                for (Player player : players) {
                    player.updateConnectivity();
                    eventDispatcher.fireEvent(new ShipUpdateEvent(player.getName(), player.getSimplifiedShipBoard()));

                    Set<Point> invalidTiles = player.getInvalidTiles();
                    if (!invalidTiles.isEmpty()) invalidShips.add(player);
                    eventDispatcher.fireEvent(new InvalidShipEvent(player.getName(), invalidTiles));
                }
                if (invalidShips.isEmpty()) setGameState(ASSIGNING_CREW);
            }
            case ASSIGNING_CREW -> {
                updatePlayersStatus();
                choosingCrewType.clear();
                for(Player player : getSortedPlayers()) {
                    eventDispatcher.fireEvent(new CrewUpdateEvent(player.getName(), player.getShipContent().crewData()));
                    Set<Point> alienCabins = player.getCabinTilesThatCanHostAliens();
                    if(!alienCabins.isEmpty()) choosingCrewType.add(player);
                    eventDispatcher.fireEvent(new AlienCabinTilesEvent(player.getName(), alienCabins));
                }
                if(choosingCrewType.isEmpty()) setGameState(PICKING_CARDS);
            }
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    private void setGameStateAfterWait(GameState nextState) {
        setGameState(WAITING);
        if (Objects.requireNonNull(nextState) == GameState.BUILDING_SHIPS) {
            generateDeck();
            for (Player player : players) {
                player.setState(BUILDING_SHIP);
                player.setNewShipBoard(boardType);
            }
            for (int i = 1; i <= players.size(); i++) {
                availablePositions.add(i);
            }
        }
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            if(gameState == WAITING) setGameState(nextState);
            scheduler.shutdown();
        }, 5, TimeUnit.SECONDS);
    }

    public void flipTimer(boolean hasPlayerFinished) {
        synchronized (timerHandler) {
            if(!timerFlipAvailable) return;
            if(!hasPlayerFinished && timerHandler.getTimesFlipped() >= boardType.getBuildingTimersCount() - 1) return;
            timerFlipAvailable = false;
            timerHandler.start();
        }
    }

    private void onTimerFinished() {
        synchronized (timerHandler) {
            if(timerHandler.getTimesFlipped() < boardType.getBuildingTimersCount()) {
                timerFlipAvailable = true;
                eventDispatcher.fireEvent(new TimerFinishedEvent(false));
            } else {
                if (!availablePositions.isEmpty()) {

                    List<Player> stillBuilding = players.stream()
                            .filter(p -> p.getState() == Player.PlayerState.BUILDING_SHIP).toList();

                    List<Integer> remainingPositions = new ArrayList<>(availablePositions);
                    Collections.shuffle(remainingPositions);

                    for (int i = 0; i < stillBuilding.size() && i < remainingPositions.size(); i++) {
                        Player player = stillBuilding.get(i);
                        int positionIndex = remainingPositions.get(i);

                        player.setState(DONE_BUILDING);
                        player.setPosition(boardType.getStartingPosition(positionIndex));
                        availablePositions.remove(positionIndex);

                        eventDispatcher.fireEvent(new DoneBuildingEvent(player.getName(), positionIndex));
                    }
                }
                eventDispatcher.fireEvent(new TimerFinishedEvent(true));
                setGameStateAfterWait(CORRECTING_SHIP);
            }
        }
    }

    public void claimPosition(Player player, int positionIndex) {
        if(player.getState() != BUILDING_SHIP) return;
        synchronized (availablePositions) {
            if(!availablePositions.contains(positionIndex)) return;

            player.setState(DONE_BUILDING);
            player.setPosition(boardType.getStartingPosition(positionIndex));
            availablePositions.remove(positionIndex);

            eventDispatcher.fireEvent(new DoneBuildingEvent(player.getName(), positionIndex));
            if(availablePositions.isEmpty()) {
                timerHandler.reset();
                setGameStateAfterWait(CORRECTING_SHIP);
            }
        }
    }

    public void removeSelectedTile(Player player, int x, int y) {
        if(!invalidShips.contains(player)) {
            Logger.logWarning("Player " + player.getName() + " doesn't have an invalid ship board.");
            return;
        }
        player.removeTile(x, y);

        Set<Point> invalidTiles = player.getInvalidTiles();
        eventDispatcher.fireEvent(new InvalidShipEvent(player.getName(), invalidTiles));

        if (invalidTiles.isEmpty()) invalidShips.remove(player);

        if (invalidShips.isEmpty()) setGameState(ASSIGNING_CREW);
    }

    public void cycleCrewTypeAt(Player player, int x, int y) {
        if(!choosingCrewType.contains(player)) {
            Logger.logWarning("Player " + player.getName() + " isn't choosing crew type.");
            return;
        }
        player.cycleCrewTypeAt(x, y);
    }
    public void doneChoosingCrew(Player player) {
        if(!choosingCrewType.contains(player)) {
            Logger.logWarning("Player " + player.getName() + " isn't choosing crew type.");
            return;
        }
        choosingCrewType.remove(player);
        eventDispatcher.fireEvent(new AlienCabinTilesEvent(player.getName(), new HashSet<>()));
        if (choosingCrewType.isEmpty()) setGameState(PICKING_CARDS);
    }

    public void pickNextCard(Player player) {
        if(currentCard != null && !currentCard.isFinished()) {
            Logger.logWarning("Cannot pick new card, previous card wasn't finished."); return;
        }
        if(!player.equals(getFirstPlayer())) {
            Logger.logWarning("Player " + player.getName() + " cannot pick a card, they are not first."); return;
        }
        if(deck.isEmpty()) {
            handleGameOver();
            return;
        }
        currentCard = deck.getFirst();
        deck.removeFirst();
        eventDispatcher.fireEvent(new CardPickedEvent(currentCard));

        currentCard.activate(this);
    }

    public void handleGameOver() {
        Set<Player> bestShipPlayers = getPlayersWithBestShip();
        List<PlayerScores> playersScores = players.stream()
                .map(p -> getPlayerScores(p, bestShipPlayers))
                .sorted(Comparator.comparingInt(PlayerScores::getFinalScore).reversed())
                .toList();
        resetToLobby();
        eventDispatcher.fireEvent(new GameOverEvent(playersScores));
    }

    public void resetToLobby() {
        this.gameState = GameState.LOBBY;
        for (Player player : players) player.setState(IDLE);
        this.deck.clear();
        this.cardPiles = null;
        currentCard = null;
        this.availableTiles.clear();
        for (int i = 0; i < 152; i++) availableTiles.add(i);
        this.availableCardPiles.clear();
        for (int i = 0; i < 3; i++) availableCardPiles.add(i);
        this.timerFlipAvailable = false;
        timerHandler.reset();
    }

    public void resetCompletely() {
        resetToLobby();
        this.players.clear();
        this.boardType = BoardType.LEVEL_TWO;
    }

    public List<PlayerInfo> getPlayersInfo(){
        return players.stream()
                .map(p -> new PlayerInfo(p.getName(),p.getProfilePictureIds(),p.getColor().toString(),p.isAdmin(),p.isReady()))
                .collect(Collectors.toList());
    }
    private PlayerInfo getPlayerInfo(Player player) {
        return new PlayerInfo(player.getName(),player.getProfilePictureIds(),player.getColor().toString(),player.isAdmin(),player.isReady());
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
    public <T extends EventModel> void addEventListener(Class<T> eventType, ModelEventListener<T> listener) {
        eventDispatcher.addListener(eventType, listener);
    }
}
