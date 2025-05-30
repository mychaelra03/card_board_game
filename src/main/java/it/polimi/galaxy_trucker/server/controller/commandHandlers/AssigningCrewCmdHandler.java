package it.polimi.galaxy_trucker.server.controller.commandHandlers;

import it.polimi.galaxy_trucker.common.messages.flight.SelectedTileMessage;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;

import java.util.Map;

/**
 * Handles commands related to assigning crew members to tiles during the game setup phase.
 * <p>
 * This includes selecting individual tiles to assign crew roles and confirming the end of the crew assignment process.
 */
public class AssigningCrewCmdHandler extends CommandHandler {

    /**
     * Constructs an {@code AssigningCrewCmdHandler} using the given game model, player mapping, and dispatcher.
     *
     * @param gameModel  the current game model
     * @param playerMap  a map linking virtual views to their respective players
     * @param dispatcher the dispatcher for sending and receiving messages
     */
    public AssigningCrewCmdHandler(GameModel gameModel, Map<VirtualView, Player> playerMap, MessageDispatcher dispatcher) {
        super(gameModel, playerMap, dispatcher);
    }

    /**
     * Registers handlers for crew assignment-related commands.
     * Supported command types:
     * <ul>
     *     <li>{@code "selected_tile"} – cycles through available crew types at a given tile</li>
     *     <li>{@code "done"} – finalizes the crew selection for the player</li>
     * </ul>
     */
    @Override
    protected void registerHandlers() {
        register("selected_tile", (view, cmd) -> handleSelectedTile(view, mapper.convertValue(cmd, SelectedTileMessage.class)));
        register("done", (view, cmd) -> handleDone(view));
    }

    private void handleSelectedTile(VirtualView senderView, SelectedTileMessage message) {
        Player player = playerMap.get(senderView);

        gameModel.cycleCrewTypeAt(player, message.getX(), message.getY());
    }

    private void handleDone(VirtualView senderView) {
        Player player = playerMap.get(senderView);

        gameModel.doneChoosingCrew(player);
    }
}
