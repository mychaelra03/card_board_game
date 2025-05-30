package it.polimi.galaxy_trucker.server.controller.listeners;

import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.model.GameModel;

/**
 * Abstract base class for handling events within the game.
 * <p>
 * Provides access to the {@link GameModel} and {@link MessageDispatcher}, allowing
 * subclasses to register and dispatch events as needed.
 * <p>
 * Subclasses are expected to define specific event-handling behavior.
 */
public abstract class EventHandler {
    protected final GameModel gameModel;
    protected final MessageDispatcher dispatcher;

    /**
     * Constructs a new {@code EventHandler} with the specified game model and message dispatcher.
     *
     * @param gameModel  the game model to interact with
     * @param dispatcher the message dispatcher used for sending and receiving messages
     */
    public EventHandler(GameModel gameModel, MessageDispatcher dispatcher) {
        this.gameModel = gameModel;
        this.dispatcher = dispatcher;
    }
}