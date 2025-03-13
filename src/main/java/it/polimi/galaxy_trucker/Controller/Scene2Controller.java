package it.polimi.galaxy_trucker.Controller;

import it.polimi.galaxy_trucker.Model.PlayerUsername;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class Scene2Controller {

    private double startX,startY,previousX,previousY,previousRotate;
    private final double DRUG_LIMIT = 2; //limit to distinguish a click from a drag

    private ImageView previousImageView = null;


    @FXML
    private AnchorPane scene2AnchorPane;




    public void initialize() {
        for(int i = 1; i<157; i++){

            Image image = new Image(getClass().getResource("/it/polimi/galaxy_trucker/risorse_grafiche/tiles/GT-new_tiles_16_for_web157.jpg").toExternalForm());

            ImageView imageView = new ImageView();
            imageView.setId(String.valueOf(i));
            imageView.setImage(image);
            imageView.setFitWidth(76);
            imageView.setFitHeight(76);
            imageView.setLayoutX( Math.random() * 800);
            imageView.setLayoutY( Math.random() * 300);
            imageView.setRotate(Math.random() * 180);

            enableEvents(imageView);

            scene2AnchorPane.getChildren().add(imageView);



        }
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

            if ( distance > DRUG_LIMIT){

                imageView.setLayoutX(event.getSceneX()- offset[0]);
                imageView.setLayoutY(event.getSceneY()- offset[1] );

                isDragging[0] = true;
            }

        });

        imageView.setOnMouseReleased(event -> {


            if (!isDragging[0]) {
                if (previousImageView != null) {
                    Image image = new Image(getClass().getResource("/it/polimi/galaxy_trucker/risorse_grafiche/tiles/GT-new_tiles_16_for web"+imageView.getId()+".jpg").toExternalForm());
                    imageView.setImage(image);
                    imageView.toFront();


                    resetImageToOriginal(previousImageView, previousX, previousY,previousRotate);
                }

                Image image = new Image(getClass().getResource("/it/polimi/galaxy_trucker/risorse_grafiche/tiles/GT-new_tiles_16_for web"+imageView.getId()+".jpg").toExternalForm());
                imageView.setImage(image);
                imageView.toFront();

                previousImageView = imageView;
                previousX = imageView.getLayoutX();
                previousY = imageView.getLayoutY();
                previousRotate = imageView.getRotate();


                animateImageToPosition(imageView, 80, 550);
            }
        });

    }
    private void animateImageToPosition(ImageView imageView, double targetX, double targetY){

        //translate
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(imageView);
        translate.setDuration(Duration.millis(500));
        translate.setToX(targetX - imageView.getLayoutX());
        translate.setToY(targetY - imageView.getLayoutY());
        translate.play();

        // rotate
        RotateTransition rotate = new RotateTransition();
        rotate.setNode(imageView);
        rotate.setDuration(Duration.millis(500));
        rotate.setByAngle(-imageView.getRotate());
        rotate.play();

        // scale
        ScaleTransition scale = new ScaleTransition();
        scale.setNode(imageView);
        scale.setDuration(Duration.millis(500));
        scale.setToX(1.7);
        scale.setToY(1.7);
        scale.play();
    }

    private void resetImageToOriginal(ImageView previousImageView, double targetX, double targetY, double rotation){
        //translate
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(previousImageView);
        translate.setDuration(Duration.millis(500));
        translate.setToX(targetX - previousImageView.getLayoutX() );
        translate.setToY(targetY - previousImageView.getLayoutY());
        translate.play();

        // rotate
        RotateTransition rotate = new RotateTransition();
        rotate.setNode(previousImageView);
        rotate.setDuration(Duration.millis(500));
        rotate.setToAngle(rotation);
        rotate.play();

        // scale
        ScaleTransition scale = new ScaleTransition();
        scale.setNode(previousImageView);
        scale.setDuration(Duration.millis(500));
        scale.setToX(1);
        scale.setToY(1);
        scale.play();

    }


}