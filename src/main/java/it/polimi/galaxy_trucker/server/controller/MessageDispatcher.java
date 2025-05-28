package it.polimi.galaxy_trucker.server.controller;

import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.VirtualView;
import it.polimi.galaxy_trucker.server.model.Player;

import java.util.Map;

public class MessageDispatcher {
    private final Map<VirtualView, Player> playerMap;

    public MessageDispatcher(Map<VirtualView, Player> playerMap) {
        this.playerMap = playerMap;
    }

    public void broadcast(Object message) {
        synchronized (playerMap) {
            for (VirtualView virtualView : playerMap.keySet()) {
                virtualView.sendMessage(message);
            }
        }
    }

    public void broadcastExcept(String playerName, Object message) {
        synchronized (playerMap) {
            for (VirtualView virtualView : playerMap.keySet()) {
                if(playerMap.get(virtualView).getName().equals(playerName)) continue;
                virtualView.sendMessage(message);
            }
        }
    }

    public void sendTo(String playerName, Object message) {
        VirtualView virtualView = getVirtualViewByPlayerName(playerName);
        virtualView.sendMessage(message);
    }

    public VirtualView getVirtualViewByPlayerName(String username) {
        for (Map.Entry<VirtualView, Player> entry : playerMap.entrySet()) {
            if (entry.getValue().getName().equals(username)) {
                return entry.getKey();
            }
        }
        Logger.logError("No VirtualView found for player: " + username);
        return null;
    }
}