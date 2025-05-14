package it.polimi.galaxy_trucker.server.events;

import it.polimi.galaxy_trucker.server.model.BoardType;

public class BoardTypeUpdateEvent extends EventModel {
    private final BoardType boardType;

    public BoardTypeUpdateEvent(BoardType boardType) {
        this.boardType = boardType;
    }

    public BoardType getBoardType() {
        return boardType;
    }
}
