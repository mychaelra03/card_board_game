package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiceThrowMessage implements GameMessage {

    private String playerName;
    private int[] dice;

    public DiceThrowMessage() {}

    public DiceThrowMessage(String playerName, int[] dice) {
        this.playerName = playerName;
        this.dice = dice;
    }

    public String getPlayerName() {
        return playerName;
    }
    public int[] getDice() {
        return dice;
    }

    @Override
    public String getType() {
        return "dice_throw";
    }
}
