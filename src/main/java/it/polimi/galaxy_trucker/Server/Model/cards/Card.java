package it.polimi.galaxy_trucker.Server.Model.cards;

import it.polimi.galaxy_trucker.GameModel;

public abstract class Card {

    private final int id;
    private final int level;
    protected GameModel board;

    public Card(int id, int level) {
        this.id = id;
        if (level < 1 || level > 2) throw new IllegalArgumentException("Card level must be between 1 and 2.");
        this.level = level;
    }

    public void assignBoard(GameModel board) {
        this.board = board;
    }

    public int getId() {
        return id;
    }
    public int getLevel() {
        return level;
    }

    public final void activate() {
        if(board == null) {
            throw new IllegalStateException("Card has not been assigned to a GameModel.");
        }
        onActivate();
    }
    protected abstract void onActivate();
    //public abstract void applyEffects();

    public abstract String getCardType();
}