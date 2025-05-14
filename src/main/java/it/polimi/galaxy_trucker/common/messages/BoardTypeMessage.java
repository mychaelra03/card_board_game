package it.polimi.galaxy_trucker.common.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.polimi.galaxy_trucker.server.model.BoardType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoardTypeMessage implements GameMessage {

    private BoardType boardType;

    public BoardTypeMessage() {}

    public BoardTypeMessage(BoardType boardType) {
       this.boardType = boardType;
    }

    public BoardType getBoardType() {
       return boardType;
    }

    @Override
    public String getType() {
        return "boardType";
    }
}
