package it.polimi.galaxy_trucker.server.controller.eventHandlers;

import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.shipBuilding.*;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.events.shipBuilding.*;
import it.polimi.galaxy_trucker.server.model.GameModel;

public class BuildingEventsHandler extends EventHandler {

    public BuildingEventsHandler(GameModel gameModel, MessageDispatcher dispatcher) {
        super(gameModel, dispatcher);

        gameModel.addEventListener(TileTakenEvent.class, this::handleTileTaken);
        gameModel.addEventListener(TileReturnedEvent.class, this::handleTileReturned);
        gameModel.addEventListener(CardsTakenEvent.class, this::handleCardsTaken);
        gameModel.addEventListener(CardsReturnedEvent.class, this::handleCardReturned);
        gameModel.addEventListener(TimerStartEvent.class, this::handleTimerStart);
        gameModel.addEventListener(TimerFinishedEvent.class, this::handleTimerFinished);
        gameModel.addEventListener(DoneBuildingEvent.class, this::handleDoneBuilding);
    }

    public void handleTileTaken(TileTakenEvent event) {
        Logger.logDebug(event.getPlayerName() + " took tile: " + event.getTileId());
        dispatcher.broadcast(new TileTakenMessage(event.getPlayerName(), event.getTileId()));
    }
    public void handleTileReturned(TileReturnedEvent event) {
        Logger.logDebug("Tile returned: " + event.getTileId());
        dispatcher.broadcast(new TileReturnedMessage(event.getTileId()));
    }
    public void handleCardsTaken(CardsTakenEvent event) {
        Logger.logDebug(event.getPlayerName() + " took cards: " + event.getPileId());
        dispatcher.broadcast(new CardsTakenMessage(event.getPlayerName(), event.getPileId()));
    }
    public void handleCardReturned(CardsReturnedEvent event) {
        Logger.logDebug("Cards returned: " + event.getPileId());
        dispatcher.broadcast(new CardsReturnedMessage(event.getPileId()));
    }
    public void handleTimerStart(TimerStartEvent event) {
        Logger.logDebug("Started timer " + event.getTimesFlipped() + " of " + event.getDuration() + " seconds");
        dispatcher.broadcast(new TimerStartMessage(event.getDuration(), event.getTimesFlipped()));
    }
    public void handleTimerFinished(TimerFinishedEvent event) {
        Logger.logDebug(event.isFinalFlip()? "Final timer ended" : "Timer ended");
        dispatcher.broadcast(new TimerFinishedMessage(event.isFinalFlip()));
    }
    public void handleDoneBuilding(DoneBuildingEvent event) {
        Logger.logDebug(event.getPlayerName() + " finished building and took position " + event.getPosition());
        dispatcher.broadcast(new DoneBuildingMessage(event.getPlayerName(), event.getPosition()));
    }
}
