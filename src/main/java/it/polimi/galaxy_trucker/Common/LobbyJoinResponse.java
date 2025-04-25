package it.polimi.galaxy_trucker.Common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LobbyJoinResponse implements GameMessage {
    public enum JoinStatus {
        SUCCESS, LOBBY_FULL, NAME_TAKEN
    }
    private JoinStatus joinStatus;

    public LobbyJoinResponse() {}

    public LobbyJoinResponse(JoinStatus joinStatus) {
        this.joinStatus = joinStatus;
    }

    public JoinStatus getJoinStatus() {
        return joinStatus;
    }

    @Override
    public String getType() {
        return "lobbyJoinResponse";
    }
}
