package it.polimi.galaxy_trucker.Common;

import java.io.Serializable;

public class FixedTileUpdate implements GameMessage {
    int x;
    int y;
    int rotation;
    String id;

    public FixedTileUpdate() {}

    public FixedTileUpdate(int x, int y, int rotation, String id) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return "fixedTile";
    }
}
