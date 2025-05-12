package it.polimi.galaxy_trucker.common.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayersInfoMessage implements GameMessage {

    private List<PlayerInfo> players;
    private String username;
    private String type = "playersInfo";


    public PlayersInfoMessage() {}

    public PlayersInfoMessage(List<PlayerInfo> players, String username) {
        this.players = players;
        this.username = username;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }

    public String getUsername() {
        return username;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
}
