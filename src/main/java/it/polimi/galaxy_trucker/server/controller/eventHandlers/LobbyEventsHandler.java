package it.polimi.galaxy_trucker.server.controller.eventHandlers;

import it.polimi.galaxy_trucker.common.messages.PlayersInfoMessage;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.events.lobby.PlayersUpdateEvent;
import it.polimi.galaxy_trucker.server.model.GameModel;

public class LobbyEventsHandler extends EventHandler {

    public LobbyEventsHandler(GameModel gameModel, MessageDispatcher dispatcher) {
        super(gameModel, dispatcher);

        gameModel.addEventListener(PlayersUpdateEvent.class, this::handlePlayersUpdate);
    }

    public void handlePlayersUpdate(PlayersUpdateEvent event) {
        dispatcher.broadcast(new PlayersInfoMessage(event.getPlayerInfoList(), event.getUsername()));
    }
}
