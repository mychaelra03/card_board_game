package it.polimi.galaxy_trucker.server.model.tiles;

/**
 * Represents the type of connector on a side of a tile.
 * Connectors determine how tiles can link together.
 */
public enum Connector {
    NONE, SINGLE, DOUBLE, UNIVERSAL;
}