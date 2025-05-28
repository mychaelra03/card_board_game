package it.polimi.galaxy_trucker.server.model.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.galaxy_trucker.server.events.flight.cards.DecisionEndEvent;
import it.polimi.galaxy_trucker.server.events.flight.cards.EngineSelectionEvent;
import it.polimi.galaxy_trucker.server.model.Player;
import it.polimi.galaxy_trucker.server.model.tiles.EngineTile;

import java.awt.*;
import java.util.List;

import static it.polimi.galaxy_trucker.server.model.Player.PlayerState.*;
import static it.polimi.galaxy_trucker.server.model.cards.Card.InputState.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class OpenSpaceCard extends Card implements EnginesHandlerCard {
    // Move forward based on engine strength.

    private List<Player> playerQueue;

    public OpenSpaceCard(@JsonProperty("id") int id, @JsonProperty("level") int level) {
        super(id, level);
    }

    @Override
    protected void onActivate() {
        this.playerQueue = board.getSortedPlayers();

        if (!playerQueue.isEmpty()) {
            setInputState(ENGINE_SELECTION);

            dispatcher.fireEvent(new EngineSelectionEvent(playerQueue.getFirst(), true));
        } else {
            setInputState(FINISHED);
        }
    }

    @Override
    public void activateEngines(Player caller, int activatedEngines, List<Point> batteryTiles) {
        verifyInputState(ENGINE_SELECTION);
        verifyPlayerTurn(caller, playerQueue);
        dispatcher.fireEvent(new DecisionEndEvent(caller.getName()));
        if(activatedEngines != batteryTiles.size()) throw new IllegalArgumentException("The number of activated engines does not match the number of batteries used.");
        int strength = (int)caller.getStrength(EngineTile.class, activatedEngines);

        if (strength != 0) {
            caller.removeBatteries(batteryTiles);
            board.movePlayerForward(caller, strength);
        } else {
            caller.setState(ELIMINATED);
        }
        playerQueue.removeFirst();

        if (!playerQueue.isEmpty()) {
            dispatcher.fireEvent(new EngineSelectionEvent(playerQueue.getFirst(), true));
        } else {
            setInputState(FINISHED);
        }
    }

    @Override
    public String getCardType() {
        return "Open Space";
    }
}
