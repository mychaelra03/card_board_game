package it.polimi.galaxy_trucker.server.controller.listeners;

import it.polimi.galaxy_trucker.common.messages.PlayersInfoMessage;
import it.polimi.galaxy_trucker.server.controller.MessageDispatcher;
import it.polimi.galaxy_trucker.server.events.lobby.PlayersUpdateEvent;
import it.polimi.galaxy_trucker.server.model.GameModel;

public class LobbyListener extends EventHandler {

    public LobbyListener(GameModel gameModel, MessageDispatcher dispatcher) {
        super(gameModel, dispatcher);
    }


    @Handles(PlayersUpdateEvent.class)
    public void handlePlayersUpdate(PlayersUpdateEvent event) {
        dispatcher.broadcast(new PlayersInfoMessage(event.getPlayerInfoList(), event.getUsername()));
    }
}
