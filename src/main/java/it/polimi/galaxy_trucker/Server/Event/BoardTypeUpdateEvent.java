package it.polimi.galaxy_trucker.Server.Event;

import it.polimi.galaxy_trucker.BoardType;
import it.polimi.galaxy_trucker.Common.PlayerInfo;

import java.util.List;

public class BoardTypeUpdateEvent extends EventModel {
    public final BoardType boardType;

    public BoardTypeUpdateEvent(BoardType boardType) {
        this.boardType = boardType;
    }

    public BoardType getBoardType() {
        return boardType;
    }
}
