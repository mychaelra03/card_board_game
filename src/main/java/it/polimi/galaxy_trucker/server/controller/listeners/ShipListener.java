package it.polimi.galaxy_trucker.server.controller.listeners;

import it.polimi.galaxy_trucker.common.messages.flight.*;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.events.flight.*;
import it.polimi.galaxy_trucker.server.model.GameModel;

public class ShipListener extends EventHandler {

    public ShipListener(GameModel gameModel, MessageDispatcher dispatcher) {
        super(gameModel, dispatcher);
    }


    @Handles(ShipUpdateEvent.class)
    private void handleShipUpdate(ShipUpdateEvent event) {
        dispatcher.broadcast(new ShipBoardMessage(event.getPlayerName(), event.getTiles()));
    }

    @Handles(InvalidShipEvent.class)
    private void handleInvalidShip(InvalidShipEvent event) {
        String playerName = event.getPlayerName();
        dispatcher.sendTo(playerName, new TilesMessage("invalid_tiles", playerName, event.getInvalidTiles()));
    }

    @Handles(AlienCabinTilesEvent.class)
    private void handleAlienCabinTiles(AlienCabinTilesEvent event) {
        String playerName = event.getPlayerName();
        dispatcher.sendTo(playerName, new TilesMessage("alien_cabin_tiles", event.getPlayerName(), event.getCabinTiles()));
    }

    @Handles(CrewUpdateEvent.class)
    private void handleCrewUpdate(CrewUpdateEvent event) {
        dispatcher.broadcast(new CrewMessage("crew", event.getPlayerName(), event.getCrew()));
    }

    @Handles(BatteriesUpdateEvent.class)
    private void handleBatteriesUpdate(BatteriesUpdateEvent event) {
        dispatcher.broadcast(new BatteriesMessage(event.getPlayerName(), event.getBatteries()));
    }

    @Handles(CargoUpdateEvent.class)
    private void handleCargoUpdate(CargoUpdateEvent event) {
        dispatcher.broadcast(new CargoMessage(event.getPlayerName(), event.getCargo()));
    }
}
