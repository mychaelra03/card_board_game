package it.polimi.galaxy_trucker.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameBoard;

public abstract class Card {

    @JsonProperty
    public final int id;
    public final int level;

    public Card(int id, int level) {
        this.id = id;
        if (level < 1 || level > 2) throw new IllegalArgumentException("Card level must be between 1 and 2.");
        this.level = level;
    }

    public int getId() {
        return id;
    }
    public int getLevel() {
        return level;
    }

    public abstract void applyEffects(GameBoard board);

    public abstract String getCardType();
}