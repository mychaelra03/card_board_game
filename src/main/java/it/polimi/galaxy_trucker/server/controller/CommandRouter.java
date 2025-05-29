package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.controller.commandHandlers.*;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;

import java.util.Map;

/**
 * The {@code CommandRouter} class is responsible for routing incoming commands
 * to the appropriate {@link CommandHandler} based on the current {@link GameModel.GameState}.
 *
 * <p>Each game state is associated with a specific handler that knows how to process commands
 * relevant to that state. This separation of concerns enables clean and maintainable code for
 * state-specific command processing.</p>
 */
public class CommandRouter {

    private final Map<GameModel.GameState, CommandHandler> handlers;

    /**
     * Constructs a {@code CommandRouter} and initializes a map of handlers
     * for each supported {@link GameModel.GameState}.
     *
     * @param gameModel  the game model shared by all handlers
     * @param playerMap  a map linking each {@link VirtualView} to its corresponding {@link Player}
     * @param dispatcher the message/event dispatcher used by all handlers
     */
    public CommandRouter(GameModel gameModel, Map<VirtualView, Player> playerMap, MessageDispatcher dispatcher) {
        handlers = Map.of(
                GameModel.GameState.LOBBY, new LobbyCmdHandler(gameModel, playerMap, dispatcher),
                GameModel.GameState.BUILDING_SHIPS, new ShipBuildingCmdHandler(gameModel, playerMap, dispatcher),
                GameModel.GameState.CORRECTING_SHIP, new CorrectingShipCmdHandler(gameModel, playerMap, dispatcher),
                GameModel.GameState.ASSIGNING_CREW, new AssigningCrewCmdHandler(gameModel, playerMap, dispatcher),
                GameModel.GameState.PICKING_CARDS, new PickingCardsCmdHandler(gameModel, playerMap, dispatcher),
                GameModel.GameState.WAITING, new WaitingCmdHandler(gameModel, playerMap, dispatcher)
        );
    }

    /**
     * Routes the incoming JSON command to the appropriate {@link CommandHandler},
     * based on the current {@link GameModel.GameState}.
     *
     * @param state  the current game state
     * @param sender the sender of the command
     * @param cmd    the JSON command to be processed
     */
    public void route(GameModel.GameState state, VirtualView sender, JsonNode cmd) {
        var handler = handlers.get(state);
        if (handler != null) handler.handle(sender, cmd);
        else Logger.logError("No handler for state: " + state);
    }
}