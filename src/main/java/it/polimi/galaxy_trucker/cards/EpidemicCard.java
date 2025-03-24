package it.polimi.galaxy_trucker.cards;

import it.polimi.galaxy_trucker.GameBoard;
import it.polimi.galaxy_trucker.Player;

import java.util.List;

public class EpidemicCard extends Card {
    // Lose crew from connected cabins.

    public EpidemicCard(int ID, int level) {
        super(ID, level);
    }

    @Override
    public void applyEffects(GameBoard board) {
        List<Player> players = board.getSortedPlayers();
        for(Player player : players) {
            player.applyEpidemicCard();
        }
    }

    @Override
    public String getCardType() {
        return "Epidemic";
    }
}
