package it.polimi.galaxy_trucker.server.controller.eventHandlers;

import it.polimi.galaxy_trucker.common.messages.flight.*;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.events.flight.*;
import it.polimi.galaxy_trucker.server.model.GameModel;

public class ShipEventsHandler extends EventHandler {

    public ShipEventsHandler(GameModel gameModel, MessageDispatcher dispatcher) {
        super(gameModel, dispatcher);

        gameModel.addEventListener(ShipUpdateEvent.class, this::handleShipUpdate);
        gameModel.addEventListener(InvalidShipEvent.class, this::handleInvalidShip);
        gameModel.addEventListener(AlienCabinTilesEvent.class, this::handleAlienCabinTiles);
        gameModel.addEventListener(CrewUpdateEvent.class, this::handleCrewUpdate);
        gameModel.addEventListener(BatteriesUpdateEvent.class, this::handleBatteriesUpdate);
        gameModel.addEventListener(CargoUpdateEvent.class, this::handleCargoUpdate);
    }

    private void handleShipUpdate(ShipUpdateEvent event) {
        dispatcher.broadcast(new ShipBoardMessage(event.getPlayerName(), event.getTiles()));
    }
    private void handleInvalidShip(InvalidShipEvent event) {
        String playerName = event.getPlayerName();
        dispatcher.sendTo(playerName, new TilesMessage("invalid_tiles", playerName, event.getInvalidTiles()));
    }
    private void handleAlienCabinTiles(AlienCabinTilesEvent event) {
        String playerName = event.getPlayerName();
        dispatcher.sendTo(playerName, new TilesMessage("alien_cabin_tiles", event.getPlayerName(), event.getCabinTiles()));
    }
    private void handleCrewUpdate(CrewUpdateEvent event) {
        dispatcher.broadcast(new CrewMessage("crew", event.getPlayerName(), event.getCrew()));
    }
    private void handleBatteriesUpdate(BatteriesUpdateEvent event) {
        dispatcher.broadcast(new BatteriesMessage(event.getPlayerName(), event.getBatteries()));
    }
    private void handleCargoUpdate(CargoUpdateEvent event) {
        dispatcher.broadcast(new CargoMessage(event.getPlayerName(), event.getCargo()));
    }
}
