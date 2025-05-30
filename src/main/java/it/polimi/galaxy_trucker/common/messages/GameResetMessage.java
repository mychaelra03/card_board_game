package it.polimi.galaxy_trucker.common.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameResetMessage implements GameMessage {

    private String playerName;

    public GameResetMessage() {}

    public GameResetMessage(String playerName) {
       this.playerName = playerName;
    }

    public String getPlayerName() {
       return playerName;
    }

    @Override
    public String getType() {
        return "game_reset";
    }
}
