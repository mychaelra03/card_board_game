package it.polimi.galaxy_trucker.client.controllers.shipBuilding;

import it.polimi.galaxy_trucker.client.ClientSession;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Objects;

public class AnimationHandler {
    private final AnchorPane mainContentPane;

    public AnimationHandler(AnchorPane mainContentPane) {
        this.mainContentPane = mainContentPane;
    }


    public void animateImageToPosition(ImageView image, double targetX, double targetY, double scaling, double rotation, int duration, int scaleDuration) {

        mainContentPane.setDisable(true);

        TranslateTransition translate = new TranslateTransition(Duration.millis(duration), image);
        translate.setToX(targetX - image.getLayoutX());
        translate.setToY(targetY - image.getLayoutY());

        RotateTransition rotate = new RotateTransition(Duration.millis(duration), image);
        rotate.setToAngle(rotation);

        ScaleTransition scale = new ScaleTransition(Duration.millis(scaleDuration), image);
        scale.setToX(scaling);
        scale.setToY(scaling);

        ParallelTransition animation = new ParallelTransition(translate, rotate, scale);


        // Re-enable interaction after animation completes
        animation.setOnFinished(_ -> mainContentPane.setDisable(false));
        animation.play();

    }


    public void rotateTile(ImageView selectedImageView, ImageView placedImageView, double angle) {
        ImageView imageToRotate = selectedImageView != null ? selectedImageView : placedImageView;

        RotateTransition rotateTransition = new RotateTransition(Duration.millis(100), imageToRotate);
        rotateTransition.setByAngle(angle);
        rotateTransition.play();
        rotateTransition.setOnFinished(_ -> {
            double rotation = imageToRotate.getRotate();
            rotation = ((rotation + 90) % 360 + 360) % 360 - 90;
            imageToRotate.setRotate(rotation);
        });
    }

    public void playTileTaken(ImageView tile) {
        tile.setDisable(true);
        tile.toFront();
        final double duration = 400;

        TranslateTransition translation = new TranslateTransition(Duration.millis(duration), tile);
        translation.setToX(150);
        translation.setToY(-75);

        ScaleTransition scale = new ScaleTransition(Duration.millis(duration / 2), tile);
        scale.setToX(1.2);
        scale.setToY(1.2);

        FadeTransition fade = new FadeTransition(Duration.millis(duration), tile);
        fade.setFromValue(1);
        fade.setToValue(0);

        ParallelTransition animation = new ParallelTransition(translation, scale, fade);
        animation.setOnFinished(_ -> tile.setVisible(false));
        animation.play();
    }

    public void playTileReturn(ImageView tile, double delay) {
        tile.setVisible(true);
        tile.toFront();
        final double duration = 600;

        TranslateTransition translation = new TranslateTransition(Duration.millis(duration), tile);
        translation.setToX(0);
        translation.setToY(0);

        RotateTransition rotation = new RotateTransition(Duration.millis(duration), tile);
        double newAngle = 90 - (Math.random() * 180);
        rotation.setByAngle(newAngle);

        ScaleTransition scale = new ScaleTransition(Duration.millis(duration + 100), tile);
        scale.setToX(1);
        scale.setToY(1);

        FadeTransition fade = new FadeTransition(Duration.millis(duration), tile);
        fade.setToValue(1);

        ParallelTransition animation = new ParallelTransition(translation, rotation, scale, fade);
        animation.setDelay(Duration.millis(delay));
        animation.setOnFinished(_ -> tile.setDisable(false));
        animation.play();
    }

    public void playCardsTaken(ImageView card, String playerName) {
        card.setDisable(true);

        final double duration = 400;

        TranslateTransition translation = new TranslateTransition(Duration.millis(duration), card);
        translation.setToX(0);
        translation.setToY(-150);

        FadeTransition cardFade = new FadeTransition(Duration.millis(duration), card);
        cardFade.setToValue(.6);

        Node parent = card.getParent();

        boolean isSelf = playerName.equals(ClientSession.getUsername());
        if(parent instanceof StackPane cardSlot && !isSelf) {
            StackPane profilePic = ClientSession.getPlayerProfilePicture(playerName, 80);
            cardSlot.getChildren().add(profilePic);
            card.toFront();

            FadeTransition fade = new FadeTransition(Duration.millis(duration + 100), profilePic);
            fade.setFromValue(0);
            fade.setToValue(.7);
            TranslateTransition translate = new TranslateTransition(Duration.millis(duration), profilePic);
            translate.setFromY(-20);
            translate.setToY(30);
            new ParallelTransition(fade, translate).play();
        }

        new ParallelTransition(translation, cardFade).play();
    }

    public void playCardsReturn(ImageView card, double delay) {
        final double duration = 400;

        TranslateTransition translation = new TranslateTransition(Duration.millis(duration), card);
        translation.setToX(0);
        translation.setToY(0);

        FadeTransition cardFade = new FadeTransition(Duration.millis(duration), card);
        cardFade.setToValue(1);

        if (card.getParent() instanceof StackPane cardSlot) {
            for(Node node : cardSlot.getChildren()) {
                if(node instanceof StackPane profilePic) {
                    FadeTransition fade = new FadeTransition(Duration.millis(duration), profilePic);
                    fade.setToValue(0);
                    fade.setOnFinished(_ -> cardSlot.getChildren().remove(node));
                    TranslateTransition translate = new TranslateTransition(Duration.millis(duration), profilePic);
                    translate.setToY(-20);
                    new ParallelTransition(fade, translate).play();
                }
            }
        }

        ParallelTransition animation = new ParallelTransition(translation, cardFade);
        animation.setOnFinished(_ -> card.setDisable(false));
        animation.setDelay(Duration.millis(delay));
        animation.play();
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
