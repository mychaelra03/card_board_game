package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerScoreMessage implements GameMessage {

    private String playerName;
    private String scoreType;
    private double score;

    public PlayerScoreMessage() {}

    public PlayerScoreMessage(String playerName, String scoreType, double score) {
        this.playerName = playerName;
        this.scoreType = scoreType;
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getScoreType() {
        return scoreType;
    }

    public double getScore() {
        return score;
    }

    @Override
    public String getType() {
        return "player_score";
    }
}
