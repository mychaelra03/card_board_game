package it.polimi.galaxy_trucker.Server.Model.cards;

import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Player;

import java.util.Collection;
import java.util.List;

public abstract class Card {

    private final int id;
    private final int level;
    protected GameModel board;

    protected enum InputState {
        NONE,
        PLANET_SELECTION,
        CANNON_SELECTION,
        ENGINE_SELECTION,
        CARGO_DECISION,
        BATTERY_DECISION,
        REWARD_DECISION,
        CREW_DECISION,
        FINISHED
    }
    private InputState inputState = InputState.NONE;

    public Card(int id, int level) {
        this.id = id;
        if (level < 1 || level > 2) throw new IllegalArgumentException("Card level must be between 1 and 2.");
        this.level = level;
    }

    public void assignBoard(GameModel board) {
        if(this.board != null) throw new IllegalStateException("Card is already assigned to a game board.");
        this.board = board;
    }

    public int getId() {
        return id;
    }
    public int getLevel() {
        return level;
    }

    public final void activate() {
        if(board == null) throw new IllegalStateException("Card has not been assigned to a GameModel.");
        onActivate();
    }
    protected abstract void onActivate();

    protected final void setInputState(InputState state) {
        this.inputState = state;
    }

    protected final void verifyPlayerTurn(Player caller, List<Player> playerQueue) {
        if(playerQueue.isEmpty()) throw new IllegalStateException("No players in queue.");
        Player currentPlayer = playerQueue.getFirst();
        verifyCaller(caller, currentPlayer);
    }
    
    protected final void verifyCaller(Player caller, Player expectedPlayer) {
        if(expectedPlayer != caller) {
            throw new IllegalStateException("It is not " + caller.getName() + "'s turn. It is currently " + expectedPlayer.getName() + "'s turn.");
        }
    }

    protected final void verifyPlayerInList(Player caller, Collection<Player> playerList, String message) {
        if(!playerList.contains(caller)) {
            throw new IllegalStateException(message);
        }
    }

    protected final void verifyInputState(InputState expectedState) {
        if(inputState != expectedState) {
            if(inputState == InputState.FINISHED) {
                throw new IllegalStateException("Card has already finished. No further input is expected.");
            }
            throw new IllegalStateException("Card is currently waiting for input: " + inputState + ". It was called a method that requires: " + expectedState + ".");
        }
    }

    public final boolean isFinished() {
        return inputState == InputState.FINISHED;
    }

    public abstract String getCardType();
}