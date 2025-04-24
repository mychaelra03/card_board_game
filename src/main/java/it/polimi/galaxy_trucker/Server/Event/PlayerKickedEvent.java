package it.polimi.galaxy_trucker.Server.Event;

import it.polimi.galaxy_trucker.Common.PlayerInfo;

public class PlayerKickedEvent extends EventModel{
    private final PlayerInfo playerInfo;

    public PlayerKickedEvent(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}
