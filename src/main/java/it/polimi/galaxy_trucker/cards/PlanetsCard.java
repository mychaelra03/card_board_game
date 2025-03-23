package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.GameBoard;
import it.polimi.galaxy_trucker.tiles.BlockType;

public class PlanetsCard extends Card {
    // Players can land to collect goods.

    private final BlockType[][] planets;
    private final int positionCost;

    public PlanetsCard(int ID, int level, BlockType[][] planets, int positionCost) {
        super(ID, level);
        if (planets.length < 1 || planets.length > 4) throw new IllegalArgumentException("There must be between 1 and 4 planets.");
        this.planets = planets;
        if (positionCost < 0) throw new IllegalArgumentException("positionCost must be a positive number.");
        this.positionCost = positionCost;
    }

    @Override
    public void applyEffects(GameBoard board) {

    }

    @Override
    public String getCardType() {
        return "Planets";
    }
}
