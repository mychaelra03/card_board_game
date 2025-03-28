package it.polimi.galaxy_trucker.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.GameModel;

public abstract class Card {

    @JsonProperty
    private final int id;
    private final int level;

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

    public abstract void applyEffects(GameModel board);

    public abstract String getCardType();
}