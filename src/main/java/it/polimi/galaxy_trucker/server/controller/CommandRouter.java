package it.polimi.galaxy_trucker.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.controller.commandHandlers.*;
import it.polimi.galaxy_trucker.server.model.GameModel;
import it.polimi.galaxy_trucker.server.model.Player;

import java.util.Map;

public class CommandRouter {

    private final Map<GameModel.GameState, CommandHandler> handlers;

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

    public void route(GameModel.GameState state, VirtualView sender, JsonNode cmd) {
        var handler = handlers.get(state);
        if (handler != null) handler.handle(sender, cmd);
        else Logger.logError("No handler for state: " + state);
    }
}