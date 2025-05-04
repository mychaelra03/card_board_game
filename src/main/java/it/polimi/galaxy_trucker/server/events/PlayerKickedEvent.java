package it.polimi.galaxy_trucker.server.events;

import it.polimi.galaxy_trucker.common.messages.PlayerInfo;

public class PlayerKickedEvent extends EventModel{
    private final PlayerInfo playerInfo;

    public PlayerKickedEvent(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}
