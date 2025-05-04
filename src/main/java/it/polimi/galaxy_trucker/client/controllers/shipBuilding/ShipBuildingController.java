package it.polimi.galaxy_trucker.client.controllers.shipBuilding;

import it.polimi.galaxy_trucker.client.popups.Popup;
import it.polimi.galaxy_trucker.common.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;


public class ShipBuildingController {


    public static final String TILE_PATH = "/textures/tiles/";
    public static final double TILE_SIZE = 83.5;

    protected static ImageView selectedImageView = null;
    protected static ImageView placedImageView = null;

    protected static Set<Rectangle> validSpots = new HashSet<>();

    protected static int lastPlacedTileColumn = 3;
    protected static int lastPlacedTileRow = 2;


    @FXML private StackPane rootPane;
    @FXML private AnchorPane mainContentPane;

    @FXML private Label settingsLabel;
    @FXML private GridPane gridPane;
    @FXML private Button rightButton,leftButton;

    public static AnimationHandler animationHandler;

    List<ImageView> imageViews = new ArrayList<>();

    public void initialize() {

        animationHandler = new AnimationHandler(mainContentPane);
        TileManager tileManager = new TileManager(gridPane);

        tileManager.updateAdjacentSockets(lastPlacedTileColumn, lastPlacedTileRow);

        // initialize covered tiles
        for(int i = 1; i<151; i++) {

            Image image = new Image(Objects.requireNonNull(getClass().getResource(TILE_PATH + "tile_back.png")).toExternalForm());

            ImageView imageView = new ImageView();
            imageView.setId(String.valueOf(i));
            imageView.setImage(image);
            imageView.setFitWidth(TILE_SIZE);
            imageView.setFitHeight(TILE_SIZE);
            imageView.setLayoutX((Math.random() * 588) + 712);
            imageView.setLayoutY((Math.random() * 530)+180);
            imageView.setRotate(Math.random() * 180);
            imageView.setCursor(Cursor.HAND);

            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(new Color(.2, .2, .2, .8));
            dropShadow.setWidth(40);
            dropShadow.setHeight(40);
            dropShadow.setRadius(19.5);

            imageView.setEffect(dropShadow);


            imageViews.add(imageView);

            tileManager.enableEvents(imageView);
        }
        Collections.shuffle(imageViews);
        mainContentPane.getChildren().addAll(imageViews);


        // initialize tiles sockets on board
        for (Node node : gridPane.getChildren()) {
            if (node instanceof Rectangle rectangle) {
                node.setOnMouseClicked(_ -> {
                    try {
                        if (!validSpots.contains(rectangle)) return;

                        if (selectedImageView != null || !placedImageView.localToScene(placedImageView.getBoundsInLocal()).intersects(rectangle.localToScene(rectangle.getBoundsInLocal()))) {
                            // "!= null" to avoid animation errors, and avoid interactions when clicking the first time with no tiles in the focus spot

                            lastPlacedTileColumn = GridPane.getColumnIndex(rectangle) != null ? GridPane.getColumnIndex(rectangle) : 0;
                            lastPlacedTileRow = GridPane.getRowIndex(rectangle) != null ? GridPane.getRowIndex(rectangle) : 0;

                            Bounds boundsInScene = rectangle.localToScene(rectangle.getBoundsInLocal());
                            double rectangleX = boundsInScene.getMinX() - mainContentPane.getLayoutX();
                            double rectangleY = boundsInScene.getMinY() - mainContentPane.getLayoutY();

                            // moving tile from Focus spot to the cardboard socket
                            if (selectedImageView != null) {
                                animationHandler.animateImageToPosition(selectedImageView, rectangleX, rectangleY, 1, 0, 200);
                                placedImageView = selectedImageView;

                                animationHandler.animateShadowEffect(placedImageView);

                                placedImageView.setMouseTransparent(true);
                                selectedImageView = null;

                            } else {
                                animationHandler.animateImageToPosition(placedImageView, rectangleX, rectangleY, 1, 0, 100);
                            }


                        } else {
                            // allowing to remove the tile from cardboard socket and positioning it in the Focus spot
                            animationHandler.animateImageToPosition(placedImageView, 312, 637, 1.7, 0, 500);

                            selectedImageView = placedImageView;
                            lastPlacedTileColumn = 0;
                            lastPlacedTileRow = 0;
                            placedImageView.setEffect(null);
                            placedImageView.setMouseTransparent(false);
                            placedImageView = null;
                        }
                    } catch (Exception e) {
                        Logger.logError(e.getMessage());
                    }
                });
            }
        }

        rightButton.setOnAction(this::onClickTurnTileRight);

        leftButton.setOnAction(this::onClickTurnTileLeft);

        settingsLabel.setCursor(Cursor.HAND);
        settingsLabel.setOnMouseClicked(_ -> Popup.newSettingsPopup().show(rootPane, mainContentPane));

        Image image = new Image(Objects.requireNonNull(getClass().getResource(TILE_PATH + "tile_starter_cabin_green.png")).toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);
        gridPane.add(imageView, 3, 2);
    }

    @FXML
    public void onClickTurnTileRight(Event event) {
        animationHandler.rotateTile(selectedImageView,placedImageView, 90);
    }

    @FXML
    public void onClickTurnTileLeft(Event event) {
        animationHandler.rotateTile(selectedImageView,placedImageView, -90);
    }
}