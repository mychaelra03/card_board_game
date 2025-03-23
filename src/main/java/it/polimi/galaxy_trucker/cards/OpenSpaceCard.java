package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.GameBoard;

public class OpenSpaceCard extends Card {
    // Move forward based on engine strength.

    public OpenSpaceCard(int ID, int level) {
        super(ID, level);
    }

    @Override
    public void applyEffects(GameBoard board) {

    }

    @Override
    public String getCardType() {
        return "Open Space";
    }
}
