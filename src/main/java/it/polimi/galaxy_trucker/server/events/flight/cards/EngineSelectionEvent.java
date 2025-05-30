package it.polimi.galaxy_trucker.server.events.flight.cards;

import it.polimi.galaxy_trucker.server.events.EventModel;
import it.polimi.galaxy_trucker.server.model.Player;

public class EngineSelectionEvent extends EventModel {

    private final Player player;
    private final boolean isOpenSpace;

    public EngineSelectionEvent(Player player, boolean isOpenSpace) {
        this.player = player;
        this.isOpenSpace = isOpenSpace;
    }

    public Player getPlayer() {
        return player;
    }
    public boolean isOpenSpace() {
        return isOpenSpace;
    }
}
