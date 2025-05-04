package it.polimi.galaxy_trucker.server.events;

import it.polimi.galaxy_trucker.common.messages.PlayerInfo;

import java.util.List;

public class PlayersUpdateEvent extends EventModel {
    public final List<PlayerInfo> playerInfoList;
    public final String username;

    public PlayersUpdateEvent(List<PlayerInfo> playerInfoList, String username) {
        this.playerInfoList = playerInfoList;
        this.username = username;
    }

    public List<PlayerInfo> getPlayerInfoList() {
        return playerInfoList;
    }

    public String getUsername() {
        return username;
    }
}
