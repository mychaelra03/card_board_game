package it.polimi.galaxy_trucker.Controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class Scene2Controller {


    public static final String TILESPATH = "/textures/tiles/";
    public static final double TAILHEIGHT = 83.5;


    private double startX,startY,previousX,previousY,previousRotate;
    private final double DRAG_LIMIT = 2; //limit to distinguish a click from a drag

    protected static ImageView selectedImageView = null;
    protected static ImageView placedImageView = null;

    protected static ArrayList<Rectangle> validSpots = new ArrayList<>();

    protected static Integer col = 3;
    protected static Integer row = 2;
    private int k;


    @FXML
    private Label settingsLabel;

    @FXML
    private GridPane gridPane;

    @FXML
    private AnchorPane scene2AnchorPane;

    @FXML
    private Button rightButton,leftButton;

    public static AnimationHandler animationHandler;
    private Settings settings;
    private TileManager tileManager;
    List<ImageView> imageViews = new ArrayList<>();


    public void animateShadowEffect(ImageView imageView) {
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.TRANSPARENT);
        innerShadow.setRadius(127);
        innerShadow.setBlurType(BlurType.GAUSSIAN);
        innerShadow.setChoke(1);
        innerShadow.setWidth(255);
        innerShadow.setHeight(255);
        imageView.setEffect(innerShadow);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(50), event -> {
                    double time = (System.currentTimeMillis() % 2000) / 2000.0 * Math.PI * 2;
                    double opacity = 0.2 + 0.1 * Math.sin(time); // Cycles between 0.1 and 0.5
                    innerShadow.setColor(new Color(1, 1, 1, opacity)); // Set new transparency
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void initialize() {

        animationHandler = new AnimationHandler(scene2AnchorPane);
        settings = new Settings(scene2AnchorPane);
        TileManager tileManager = new TileManager(gridPane);


        tileManager.availableSockets();

        // initialize covered tiles
        for(int i = 1; i<151; i++) {

            Image image = new Image(Objects.requireNonNull(getClass().getResource(TILESPATH + "tile_back.jpg")).toExternalForm());

            ImageView imageView = new ImageView();
            imageView.setId(String.valueOf(i));
            imageView.setImage(image);
            imageView.setFitWidth(TAILHEIGHT);
            imageView.setFitHeight(TAILHEIGHT);
            imageView.setLayoutX((Math.random() * 588) + 712);
            imageView.setLayoutY((Math.random() * 530)+180);
            imageView.setRotate(Math.random() * 180);
            imageView.setCursor(Cursor.HAND);


            imageViews.add(imageView);


            tileManager.enableEvents(imageView);

        }
        Collections.shuffle(imageViews);
        scene2AnchorPane.getChildren().addAll(imageViews);


            // initialize tails sockets on board
            for (javafx.scene.Node node : gridPane.getChildren()) {
                if (node instanceof Rectangle ) {
                    node.setOnMouseClicked(event -> {
                        try {
                            if(validSpots.contains((Rectangle) node)) {
                                if (selectedImageView != null || !placedImageView.localToScene(placedImageView.getBoundsInLocal()).intersects(node.localToScene(node.getBoundsInLocal()))) {    // !=null to avoid animation errors, and avoid interactions when clicking the first time with no tails in the focus spot


                                    col = GridPane.getColumnIndex(node);
                                    row = GridPane.getRowIndex(node);

                                    row = (row == null) ? 0 : row;
                                    col = (col == null) ? 0 : col;

                                    Bounds boundsInScene = node.localToScene(node.getBoundsInLocal());
                                    double rectangleX = boundsInScene.getMinX() - scene2AnchorPane.getLayoutX();
                                    double rectangleY = boundsInScene.getMinY() - scene2AnchorPane.getLayoutY();

                                    //moving tail from Focus spot to the cardboard socket
                                    if (selectedImageView != null) {
                                        animationHandler.animateImageToPosition(selectedImageView, rectangleX, rectangleY, 1, 0, 200);
                                        placedImageView = selectedImageView;


                                        //InnerShadow inner = new InnerShadow();
                                        //inner.setColor(new Color(1, 1, 1, .3));
                                        //inner.setChoke(0);
                                        //inner.setWidth(94);
                                        //inner.setHeight(115);
                                        //inner.setRadius(52);

                                        //InnerShadow inner = new InnerShadow();
                                        //inner.setColor(new Color(1, 1, 1, .3));
                                        //inner.setBlurType(BlurType.GAUSSIAN);
                                        //inner.setChoke(1);
                                        //inner.setWidth(255);
                                        //inner.setHeight(255);
                                        //inner.setRadius(127);

                                        //placedImageView.setEffect(inner);
                                        animateShadowEffect(placedImageView);


                                        placedImageView.setMouseTransparent(true);
                                        selectedImageView = null;

                                    } else {
                                        animationHandler.animateImageToPosition(placedImageView, rectangleX, rectangleY, 1, 0, 100);
                                    }


                                } else {
                                    // allowing to remove the tail from cardboard socket and positioning it in the Focus spot

                                    animationHandler.animateImageToPosition(placedImageView, 312, 637, 1.7, 0, 500);


                                    selectedImageView = placedImageView;
                                    col = 0;
                                    row = 0;
                                    placedImageView.setEffect(null);
                                    placedImageView.setMouseTransparent(false);
                                    placedImageView = null;

                                }
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                    });

                }
            }


            rightButton.setOnAction(event -> {
                try {
                    onClickTurnTileRight(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });


            leftButton.setOnAction(event -> {
                try {
                    onClickTurnTileLeft(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            settingsLabel.setCursor(Cursor.HAND);
            settingsLabel.setOnMouseClicked(event -> {
                try {
                    settings.showPopupSettings(scene2AnchorPane);
                }catch (IOException e){
                    throw new RuntimeException(e);
                }
            });

        Image image = new Image(Objects.requireNonNull(getClass().getResource(TILESPATH + "tile_starter_cabin_green.jpg")).toExternalForm());
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(TAILHEIGHT);
        imageView.setFitHeight(TAILHEIGHT);
        gridPane.add(imageView, 3, 2);
    }

    @FXML
    public void onClickTurnTileRight(Event event) throws IOException {
        animationHandler.onClickTurnTileRight(selectedImageView,placedImageView);
    }

    @FXML
    public void onClickTurnTileLeft(Event event) throws IOException {
        animationHandler.onClickTurnTileLeft(selectedImageView,placedImageView);
    }



}