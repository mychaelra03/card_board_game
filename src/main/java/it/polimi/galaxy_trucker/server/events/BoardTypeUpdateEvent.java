package it.polimi.galaxy_trucker.server.events;

import it.polimi.galaxy_trucker.BoardType;

public class BoardTypeUpdateEvent extends EventModel {
    public final BoardType boardType;

    public BoardTypeUpdateEvent(BoardType boardType) {
        this.boardType = boardType;
    }

    public BoardType getBoardType() {
        return boardType;
    }
}
