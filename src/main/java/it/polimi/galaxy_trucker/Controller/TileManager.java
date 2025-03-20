package it.polimi.galaxy_trucker.Controller;

import it.polimi.galaxy_trucker.Client;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

import static it.polimi.galaxy_trucker.Controller.Scene2Controller.*;

public class TileManager {

    public static final String TILESPATH = "/textures/tiles/";
    private double startX,startY,previousX,previousY,previousRotate;
    private final double DRAG_LIMIT = 2; //limit to distinguish a click from a drag


    private GridPane gridPane;
    private AnchorPane scene2AnchorPane;

    public Client client = new Client();



    public TileManager(GridPane gridPane) {
       this.gridPane = gridPane;
    }
    public void enableEvents (ImageView imageView){
        final double[] offset = new double[2];
        final boolean[] isDragging = {false};

        imageView.setOnMousePressed(event -> {

            startX = event.getSceneX();
            startY = event.getSceneY();

            offset[0] = event.getSceneX() - imageView.getLayoutX();
            offset[1] = event.getSceneY() - imageView.getLayoutY();

            isDragging[0] = false;

        });

        imageView.setOnMouseDragged(event -> {

            double endX = event.getSceneX();
            double endY = event.getSceneY();
            double distance = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));

            //  to avoid unwanted small movements to count as a drag instead of click
            if ( distance > DRAG_LIMIT){

                imageView.setLayoutX(event.getSceneX()- offset[0]);
                imageView.setLayoutY(event.getSceneY()- offset[1] );

                isDragging[0] = true;
            }

            // doesn't allow the tales to be moved outside the
            if( imageView.getLayoutX() <= 712){
                imageView.setLayoutX(712);
            }
            if (imageView.getLayoutX()>=1300){
                imageView.setLayoutX(1300);
            }
            if (imageView.getLayoutY() >= 700){
                imageView.setLayoutY(700);
            }
            if (imageView.getLayoutY()<= 170){
                imageView.setLayoutY(170);
            }

        });

        imageView.setOnMouseReleased(event -> {

            if (!isDragging[0]) {


                Image image = new Image(Objects.requireNonNull(getClass().getResource(TILESPATH + "tile_" + imageView.getId() + ".jpg")).toExternalForm());
                imageView.setImage(image);
                imageView.toFront();

                // allows to switch from the tile in the Focus spot to one of the tiles in the choosing pit
                if (selectedImageView != null) {

                    enableEvents(selectedImageView);

                    animationHandler.resetImageToOriginal(selectedImageView, previousX, previousY,previousRotate);

                }

                selectedImageView = imageView;
                previousX = imageView.getLayoutX();
                previousY = imageView.getLayoutY();
                previousRotate = imageView.getRotate();

                // move a tail from the choosing pit to the Focus spot
                double rotation = -(selectedImageView.getRotate()%360);
                animationHandler.animateImageToPosition(selectedImageView, 312, 637, 1.7, rotation,500);

                // disable mouse events on the imageView in the Focus spot
                selectedImageView.setOnMouseDragged(null);
                selectedImageView.setOnMouseReleased(null);

                // disable the events on the placedImageView so it doesn't activate the animation back to the focus spot
                if (placedImageView != null){
                    placedImageView.setDisable(true);
                    placedImageView.setEffect(null);
                }

                availableSockets();
                validSpots.stream()
                        .forEach(rect -> {
                            rect.setFill(new Color(1, 1, 1, .2));
                            rect.setCursor(Cursor.HAND);
                        });



                // by doing this we permanently fix the previously added tail to the cardboard when bringing a tail from the choosing pot to the focus spot
                //we remove the rectangle of the gridpane in the position of the imageView placed, not allowing it to be clicked anymore.
                removeRectFromGridPane(gridPane,col,row);

                client.sendTileDataWhenFixed(col,row, placedImageView.getId(), (int) (((placedImageView.getRotate()%360+360)%360)/90));

            }
        });

    }


    private void removeRectFromGridPane (GridPane gridPane, Integer col, Integer row) {
        javafx.scene.Node nodeToRemove = null;

        // loop through the Rectangles inside the gidPane till it finds the one corresponding to col,row values previously saved in the onMouseClick at line 70
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (node instanceof Rectangle) {
                Integer colCycle = GridPane.getColumnIndex(node);
                Integer rowCycle = GridPane.getRowIndex(node);

                // when the rowCycle and colCycle var are null, their value in the grid is actually 0 (the following 2 lines will fix this)
                rowCycle = (rowCycle == null) ? 0 : rowCycle;
                colCycle = (colCycle == null) ? 0 : colCycle;

                if (colCycle.equals(col) && rowCycle.equals(row)) {
                    nodeToRemove = node;
                    break;
                }
            }
        }
        if (nodeToRemove != null) {
            gridPane.getChildren().remove(nodeToRemove);
        }
    }

    public void availableSockets(){
        for(int k=-1; k<=1; k++){
            for (javafx.scene.Node node : gridPane.getChildren()) {
                if (node instanceof Rectangle) {
                    Integer colRecAvail = GridPane.getColumnIndex(node);
                    Integer rowRecAvail = GridPane.getRowIndex(node);

                    rowRecAvail = (rowRecAvail == null) ? 0 : rowRecAvail;
                    colRecAvail = (colRecAvail == null) ? 0 : colRecAvail;

                    if ( colRecAvail.equals( col + k ) && rowRecAvail.equals(row)) {

                        validSpots.add((Rectangle) node);
                    }
                    if(colRecAvail.equals( col) && rowRecAvail.equals(row + k)){
                        validSpots.add((Rectangle) node);
                    }
                }
            }
        }
    }

}