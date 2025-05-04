package it.polimi.galaxy_trucker.client.controllers.shipBuilding;

import javafx.animation.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnimationHandler {
    private AnchorPane scene2AnchorPane;

    public AnimationHandler(AnchorPane scene2AnchorPane) {
        this.scene2AnchorPane = scene2AnchorPane;
    }


    public void animateImageToPosition(ImageView imageView, double targetX, double targetY, double scaling, double rotation, int duration){

        scene2AnchorPane.setDisable(true);

        TranslateTransition translate = new TranslateTransition();
        translate.setNode(imageView);
        translate.setDuration(Duration.millis(duration));
        translate.setInterpolator(Interpolator.LINEAR);
        translate.setToX(targetX - imageView.getLayoutX());
        translate.setToY(targetY - imageView.getLayoutY());

        RotateTransition rotate = new RotateTransition();
        rotate.setNode(imageView);
        rotate.setDuration(Duration.millis(500));
        rotate.setByAngle(rotation);

        ScaleTransition scale = new ScaleTransition();
        scale.setNode(imageView);
        scale.setDuration(Duration.millis(500));
        scale.setToX(scaling);
        scale.setToY(scaling);

        ParallelTransition animation = new ParallelTransition(translate, rotate, scale);


        // Re-enable interaction after animation completes
        animation.setOnFinished(_ -> scene2AnchorPane.setDisable(false));
        animation.play();

    }

    public void resetImageToOriginal(ImageView imageView, double targetX, double targetY, double previousRotate) {
        scene2AnchorPane.setDisable(true); // Disable player interaction

        TranslateTransition translate = new TranslateTransition();
        translate.setNode(imageView);
        translate.setDuration(Duration.millis(500));
        translate.setToX(targetX - imageView.getLayoutX() );
        translate.setToY(targetY - imageView.getLayoutY());

        RotateTransition rotate = new RotateTransition();
        rotate.setNode(imageView);
        rotate.setDuration(Duration.millis(500));
        rotate.setByAngle(-(imageView.getRotate()%360) + previousRotate % 360);

        ScaleTransition scale = new ScaleTransition();
        scale.setNode(imageView);
        scale.setDuration(Duration.millis(500));
        scale.setToX(1);
        scale.setToY(1);

        ParallelTransition animation = new ParallelTransition(translate, rotate,scale);

        // Re-enable interaction after animation completes
        animation.setOnFinished(_ -> scene2AnchorPane.setDisable(false));

        animation.play();

    }

    public void rotateTile(ImageView selectedImageView, ImageView placedImageView, double angle) {
        ImageView imageToRotate = selectedImageView != null ? selectedImageView : placedImageView;

        RotateTransition rotateTransition = new RotateTransition(Duration.millis(100), imageToRotate);
        rotateTransition.setByAngle(angle);
        rotateTransition.play();
    }


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
                    double opacity = 0.2 + 0.1 * Math.sin(time); // Cycles between 0.1 and 0.3
                    innerShadow.setColor(new Color(1, 1, 1, opacity));
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

}
