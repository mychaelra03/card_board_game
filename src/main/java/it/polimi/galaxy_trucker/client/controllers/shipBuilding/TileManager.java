package it.polimi.galaxy_trucker.client.controllers.shipBuilding;

import it.polimi.galaxy_trucker.BoardType;
import it.polimi.galaxy_trucker.client.VirtualServer;
import it.polimi.galaxy_trucker.common.messages.shipBuilding.TileFixedMessage;
import it.polimi.galaxy_trucker.common.messages.shipBuilding.TileReturnedMessage;
import it.polimi.galaxy_trucker.common.messages.shipBuilding.TileTakenMessage;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

import static it.polimi.galaxy_trucker.client.controllers.shipBuilding.ShipBuildingController.*;

public class TileManager    {

    public static final String TILESPATH = "/textures/tiles/";

    private final AnchorPane mainContentPane;
    private final GridPane gridPane;
    private final Rectangle selectedTileSlot;

    private Integer gridX = null, gridY = null;

    private final VirtualServer virtualServer = VirtualServer.getInstance();

    public TileManager(AnchorPane mainContentPane, GridPane gridPane, Rectangle selectedTileSlot) {
        this.mainContentPane = mainContentPane;
        this.gridPane = gridPane;
        this.selectedTileSlot = selectedTileSlot;
    }

    public void enableEvents (ImageView tileImage) {

        final double[] offsetX = new double[1];
        final double[] offsetY = new double[1];
        final double[] pressX = new double[1];
        final double[] pressY = new double[1];

        tileImage.setOnMousePressed(event -> {
            tileImage.toFront();
            offsetX[0] = event.getSceneX() - tileImage.getLayoutX();
            offsetY[0] = event.getSceneY() - tileImage.getLayoutY();
            pressX[0] = event.getSceneX();
            pressY[0] = event.getSceneY();
        });

        tileImage.setOnMouseDragged(event -> {
            tileImage.setLayoutX(event.getSceneX() - offsetX[0]);
            tileImage.setLayoutY(event.getSceneY() - offsetY[0]);

            if (tileImage.getLayoutX() < 712) tileImage.setLayoutX(712);
            if (tileImage.getLayoutX() > 1300) tileImage.setLayoutX(1300);
            if (tileImage.getLayoutY() > 700) tileImage.setLayoutY(700);
            if (tileImage.getLayoutY() < 170) tileImage.setLayoutY(170);

        });

        tileImage.setOnMouseReleased(event -> {
            double deltaX = event.getSceneX() - pressX[0];
            double deltaY = event.getSceneY() - pressY[0];
            if (Math.hypot(deltaX, deltaY) < 5) {
                finalizeSelectedTile();
                virtualServer.sendMessage(new TileTakenMessage(null, Integer.parseInt(tileImage.getId())));
            }
        });

    }

    protected void takeTile(ImageView tile) {

        tile.setImage(new Image(Objects.requireNonNull(getClass().getResource(TILESPATH + "tile_" + tile.getId() + ".png")).toExternalForm()));
        tile.toFront();

        selectedTile = tile;

        // move a tile from the choosing pit to the Focus spot
        moveToSelectedSlot();

        // disable mouse events on the imageView in the Focus spot
        selectedTile.setDisable(true);

        // disable the events on the placedImageView so it doesn't activate the animation back to the focus spot

        // by doing this we permanently fix the previously added tile to the cardboard when bringing a tile from the choosing pot to the focus spot
        // we remove the rectangle of the grid pane in the position of the imageView placed, not allowing it to be clicked anymore.
    }

    protected void finalizeSelectedTile() {

        if(selectedTile == null) return;

        if(gridX == null && gridY == null) {
            // returns the tile to the table
            selectedTile.setDisable(true);
            virtualServer.sendMessage(new TileReturnedMessage(0));
        }
        else {
            // fixes the tile to the ship
            selectedTile.setDisable(true);
            selectedTile.setEffect(null);
            int degrees = (int)selectedTile.getRotate();
            int rotation = ((degrees / 90) % 4 + 4) % 4;

            updateAdjacentCells(gridX, gridY);
            virtualServer.sendMessage(new TileFixedMessage(0, gridX, gridY, rotation));
            gridX = gridY = null;
        }

        selectedTileSlot.setCursor(Cursor.DEFAULT);
        selectedTile = null;
    }

    public void initializeGridPane(BoardType shipType) {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 7; x++) {
                if (isInsideShip(shipType, x, y)) {

                    StackPane cell = new StackPane();
                    cell.setStyle("-fx-background-color: red; -fx-background-radius: 5");

                    gridPane.add(cell, x, y);

                    cell.setOnMouseClicked(_ -> {

                        if (selectedTile == null || !validCells.contains(cell)) return;

                        int colIndex = GridPane.getColumnIndex(cell) != null ? GridPane.getColumnIndex(cell) : 0;
                        int rowIndex = GridPane.getRowIndex(cell) != null ? GridPane.getRowIndex(cell) : 0;

                        if(gridX != null && gridY != null && gridX == colIndex && gridY == rowIndex) {
                            moveToSelectedSlot();
                            return;
                        }

                        gridX = GridPane.getColumnIndex(cell) != null ? GridPane.getColumnIndex(cell) : 0;
                        gridY = GridPane.getRowIndex(cell) != null ? GridPane.getRowIndex(cell) : 0;

                        Bounds boundsInScene = cell.localToScene(cell.getBoundsInLocal());
                        double cellX = boundsInScene.getMinX() - mainContentPane.getLayoutX();
                        double cellY = boundsInScene.getMinY() - mainContentPane.getLayoutY();

                        // moving tile from Focus spot to the cardboard socket
                        animationHandler.animateImageToPosition(selectedTile, cellX, cellY, 1, selectedTile.getRotate(), 150, 300);

                        animationHandler.animateShadowEffect(selectedTile);

                        selectedTileSlot.setCursor(Cursor.HAND);
                        selectedTile.setMouseTransparent(true);
                    });
                }
            }
        }
    }

    public void moveToSelectedSlot() {
        gridX = gridY = null;
        if(selectedTile == null) return;
        selectedTile.setMouseTransparent(false);
        animationHandler.animateImageToPosition(selectedTile, selectedTileSlot.getLayoutX(), selectedTileSlot.getLayoutY(), 1.7, 0,500, 500);
    }

    private boolean isInsideShip(BoardType shipType, int x, int y) {
        if(y < 0 || x < 0 || y >= 5 || x >= 7) return false;
        if(shipType == BoardType.LEARNING || shipType == BoardType.LEVEL_ONE) {
            int[][] type1 = {
                    {0, 0, 0, 1, 0, 0, 0},
                    {0, 0, 1, 1, 1, 0, 0},
                    {0, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 1, 1, 1, 0},
                    {0, 1, 1, 0, 1, 1, 0}
            };
            return type1[y][x] == 1;
        }
        if(shipType == BoardType.LEVEL_TWO) {
            int[][] type2 = {
                    {0, 0, 1, 0, 1, 0, 0},
                    {0, 1, 1, 1, 1, 1, 0},
                    {1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 0, 1, 1, 1}
            };
            return type2[y][x] == 1;
        }
        throw new RuntimeException("Invalid ship type: " + shipType);
    }

    public void updateAdjacentCells(int x, int y) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof StackPane cell) {
                int nodeCol = GridPane.getColumnIndex(cell) != null ? GridPane.getColumnIndex(cell) : 0;
                int nodeRow = GridPane.getRowIndex(cell) != null ? GridPane.getRowIndex(cell) : 0;

                boolean isAdjacentHorizontally = Math.abs(nodeCol - x) == 1 && nodeRow == y;
                boolean isAdjacentVertically = Math.abs(nodeRow - y) == 1 && nodeCol == x;

                if (isAdjacentHorizontally || isAdjacentVertically) {
                    validCells.add(cell);
                }
            }
        }

        validCells.forEach(cell -> {
            cell.setStyle("-fx-background-color: rgba(255, 255, 255, .2);");
            cell.setCursor(Cursor.HAND);
        });
        removePaneFromGrid(x, y);
    }

    private void removePaneFromGrid(int x, int y) {
        Node nodeToRemove = null;

        // loop through the Panes inside the gidPane till it finds the one corresponding to col,row values
        for (Node node : gridPane.getChildren()) {
            if (node instanceof StackPane cell) {
                int cellCol = GridPane.getColumnIndex(cell) != null ? GridPane.getColumnIndex(cell) : 0;
                int cellRow = GridPane.getRowIndex(cell) != null ? GridPane.getRowIndex(cell) : 0;

                if (cellCol == x && cellRow == y) {
                    nodeToRemove = cell;
                    break;
                }
            }
        }
        if (nodeToRemove != null) {
            validCells.remove(nodeToRemove);
            gridPane.getChildren().remove(nodeToRemove);
        }
    }
}