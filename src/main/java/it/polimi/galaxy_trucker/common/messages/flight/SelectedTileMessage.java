package it.polimi.galaxy_trucker.common.messages.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.common.messages.GameMessage;

import java.awt.*;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SelectedTileMessage implements GameMessage {

    int x;
    int y;

    public SelectedTileMessage() {}

    public SelectedTileMessage(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    @Override
    public String getType() {
        return "selected_tile";
    }
}
