package it.polimi.galaxy_trucker.Client.Controller.Scene2;

import javafx.animation.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;

public class AnimationHandler {
    private AnchorPane scene2AnchorPane;

    public AnimationHandler(AnchorPane scene2AnchorPane) {
        this.scene2AnchorPane = scene2AnchorPane;
    }


    public void animateImageToPosition(ImageView imageView, double targetX, double targetY, double scaling, double rotation, int duration){

        scene2AnchorPane.setDisable(true);
        // translate
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(imageView);
        translate.setDuration(Duration.millis(duration));
        translate.setInterpolator(Interpolator.LINEAR);
        translate.setToX(targetX - imageView.getLayoutX());
        translate.setToY(targetY - imageView.getLayoutY());

        // rotate
        RotateTransition rotate = new RotateTransition();
        rotate.setNode(imageView);
        rotate.setDuration(Duration.millis(500));
        rotate.setByAngle(rotation);

        // scale
        ScaleTransition scale = new ScaleTransition();
        scale.setNode(imageView);
        scale.setDuration(Duration.millis(500));
        scale.setToX(scaling);
        scale.setToY(scaling);

        ParallelTransition animation = new ParallelTransition(translate, rotate, scale);


        animation.setOnFinished(event ->
            // Re-enable interaction after animation completes
            scene2AnchorPane.setDisable(false)
        );
        animation.play();

    }

    public void resetImageToOriginal(ImageView imageView, double targetX, double targetY, double previousRotate){
        scene2AnchorPane.setDisable(true); // Disable player interaction

        //translate
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(imageView);
        translate.setDuration(Duration.millis(500));
        translate.setToX(targetX - imageView.getLayoutX() );
        translate.setToY(targetY - imageView.getLayoutY());

        // rotate
        RotateTransition rotate = new RotateTransition();
        rotate.setNode(imageView);
        rotate.setDuration(Duration.millis(500));
        rotate.setByAngle(-(imageView.getRotate()%360) + previousRotate % 360);

        // scale
        ScaleTransition scale = new ScaleTransition();
        scale.setNode(imageView);
        scale.setDuration(Duration.millis(500));
        scale.setToX(1);
        scale.setToY(1);

        ParallelTransition animation = new ParallelTransition(translate, rotate,scale);

        // Re-enable interaction after animation completes
        animation.setOnFinished(event -> {
            scene2AnchorPane.setDisable(false);
        });

        animation.play();

    }



    public void onClickTurnTileLeft(ImageView selectedImageView, ImageView placedImageView) throws IOException {

        if (selectedImageView != null) {
            RotateTransition rotateTransition = new RotateTransition(Duration.millis(100), selectedImageView);
            rotateTransition.setByAngle(-90);
            rotateTransition.play();
        }else {
            RotateTransition rotateTransition = new RotateTransition(Duration.millis(100), placedImageView);
            rotateTransition.setByAngle(-90);
            rotateTransition.play();
        }
    }



    public void onClickTurnTileRight(ImageView selectedImageView,ImageView placedImageView) throws IOException {

        if (selectedImageView != null) {
            RotateTransition rotateTransition = new RotateTransition(Duration.millis(100), selectedImageView);
            rotateTransition.setByAngle(90);
            rotateTransition.play();
        } else {

            RotateTransition rotateTransition = new RotateTransition(Duration.millis(100), placedImageView);
            rotateTransition.setByAngle(90);
            rotateTransition.play();
        }
    }

}
