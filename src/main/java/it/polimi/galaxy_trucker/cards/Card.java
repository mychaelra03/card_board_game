package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.GameBoard;
import it.polimi.galaxy_trucker.Player;

public abstract class Card {

    private final int ID;
    private final int level;

    public Card(int ID, int level) {
        this.ID = ID;
        if (level < 1 || level > 2) throw new IllegalArgumentException("Card level must be between 1 and 2.");
        this.level = level;
    }

    public int getID() {
        return ID;
    }
    public int getLevel() {
        return level;
    }

    public abstract void applyEffects(GameBoard board);

    public abstract String getCardType();
}