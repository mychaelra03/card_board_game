package it.polimi.galaxy_trucker.client.controllers.flight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.client.ClientSession;
import it.polimi.galaxy_trucker.client.popups.Popup;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.server.model.BoardType;
import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

public class FlightAnim {

    private static final Queue<Runnable> animationQueue = new LinkedList<>();
    private static boolean isAnimationPlaying = false;

    private static final Map<Integer, Point> boardSlotCoordinates = new HashMap<>();
    private static final Map<String, StackPane> playerPawns = new HashMap<>();

    private static void playNextAnimation() {
        if (isAnimationPlaying || animationQueue.isEmpty()) return;

        Runnable next = animationQueue.poll();
        isAnimationPlaying = true;
        next.run();
    }

    public boolean isAnimationPlaying() {
        return isAnimationPlaying;
    }

    public static void queueUpdateStatus(Label statusLabel, String message, double duration) {
        animationQueue.offer(() -> updateStatus(statusLabel, message, duration));
        playNextAnimation();
    }
    private static void updateStatus(Label statusLabel, String message, double duration) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), statusLabel);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> statusLabel.setText(message));

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), statusLabel);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.millis(duration));

        SequentialTransition animation = new SequentialTransition(fadeOut, fadeIn, pause);
        animation.setOnFinished(_ -> {
            isAnimationPlaying = false;
            playNextAnimation();
        });
        animation.play();
    }

    public static void movePlayerPawn(String playerName, int position, ImageView gameBoardImage) {
        double originalWidth = 1055; // Width of the original PNG
        double scale = gameBoardImage.getFitWidth() / originalWidth;

        StackPane pawn = playerPawns.get(playerName);

        int trackLength = boardSlotCoordinates.size();
        int newPosition = (position % trackLength + trackLength) % trackLength;
        double gameBoardX = gameBoardImage.getLayoutX();
        double gameBoardY = gameBoardImage.getLayoutY();
        double newX = gameBoardX + boardSlotCoordinates.get(newPosition).x * scale - pawn.getPrefWidth() / 2;
        double newY = gameBoardY + boardSlotCoordinates.get(newPosition).y * scale - pawn.getPrefHeight() / 2;

        queuePawnMove(pawn, newX, newY);
    }

    private static void queuePawnMove(StackPane pawn, double x, double y) {
        animationQueue.offer(() -> playPawnMove(pawn, x, y));
        playNextAnimation();
    }

    private static void playPawnMove(StackPane pawn, double x, double y) {

        TranslateTransition move = new TranslateTransition(Duration.millis(500), pawn);
        move.setToX(x - pawn.getLayoutX());
        move.setToY(y - pawn.getLayoutY());

        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), pawn);
        fadeOut.setToValue(.7);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), pawn);
        scaleOut.setToX(1.2);
        scaleOut.setToY(1.2);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), pawn);
        fadeIn.setDelay(Duration.millis(350));
        fadeIn.setToValue(1);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), pawn);
        scaleIn.setDelay(Duration.millis(350));
        scaleIn.setToX(1);
        scaleIn.setToY(1);

        ParallelTransition animation = new ParallelTransition(move, fadeOut, scaleOut, fadeIn, scaleIn);
        animation.setOnFinished(_ -> {
            isAnimationPlaying = false;
            playNextAnimation();
        });
        animation.play();
    }

    public static void setShadowToCard(ImageView card) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(new Color(0, 0, 0, .5));
        shadow.setWidth(100);
        shadow.setHeight(100);
        shadow.setRadius(20);
        card.setEffect(shadow);
    }

    public static void markTile(ImageView tile, Color color) {
        tile.setCursor(javafx.scene.Cursor.HAND);
        InnerShadow shadow = new InnerShadow();
        shadow.setColor(color);
        shadow.setBlurType(BlurType.THREE_PASS_BOX);
        shadow.setWidth(50);
        shadow.setHeight(50);
        shadow.setRadius(10);
        shadow.setChoke(1.0);
        tile.setEffect(shadow);
    }

    public static void unmarkTiles(GridPane shipGridP1) {
        for(Node node : shipGridP1.getChildren()) {
            if(node instanceof ImageView tile) {
                tile.setEffect(null);
                tile.setOnMouseClicked(null);
                tile.setCursor(Cursor.DEFAULT);
            }
        }
    }

    public static void queueCardTaken(ImageView card, StackPane deckPane, Node target, Image newImage) {
        animationQueue.offer(() -> playCardTaken(card, deckPane, target, newImage));
        playNextAnimation();
    }

    private static void playCardTaken(ImageView card, StackPane deckPane, Node target, Image newImage) {
        final double duration = 600;

        card.toFront();

        Bounds targetBounds = target.localToScene(target.getBoundsInLocal());
        Bounds parentBounds = deckPane.localToScene(deckPane.getBoundsInLocal());

        double targetX = targetBounds.getMinX() - parentBounds.getMinX();
        double targetY = targetBounds.getMinY() - parentBounds.getMinY();

        TranslateTransition translation = new TranslateTransition(Duration.millis(duration), card);
        translation.setToX(targetX);
        translation.setToY(targetY);

        RotateTransition rotation = new RotateTransition(Duration.millis(duration), card);
        rotation.setToAngle(0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(duration / 2), card);
        scaleIn.setToX(0);
        scaleIn.setToY(1.05);
        scaleIn.setOnFinished(_ -> card.setImage(newImage));

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(duration / 2), card);
        scaleOut.setDelay(Duration.millis(duration / 2));
        scaleOut.setToX(1.1);
        scaleOut.setToY(1.1);

        ParallelTransition animation = new ParallelTransition(translation, rotation, scaleIn, scaleOut);
        animation.setOnFinished(_ -> {
            isAnimationPlaying = false;
            playNextAnimation();
        });
        animation.play();
    }

    public static void playCardRemove(ImageView card) {
        final double duration = 600;

        setShadowToCard(card);

        TranslateTransition translation = new TranslateTransition(Duration.millis(duration), card);
        translation.setByX(-100);

        RotateTransition rotation = new RotateTransition(Duration.millis(duration), card);
        rotation.setByAngle(-70);

        FadeTransition fade = new FadeTransition(Duration.millis(duration), card);
        fade.setToValue(0);

        ParallelTransition animation = new ParallelTransition(translation, rotation, fade);
        animation.setOnFinished(_ -> {
            if (card.getParent() instanceof Pane pane) {
                pane.getChildren().remove(card);
            }
        });
        animation.play();
    }

    public static void queuePlanetsPopup(ImageView currentCard, List<String> planetChosenBy) {
        animationQueue.offer(() -> showPlanetsPopup(currentCard, planetChosenBy));
        playNextAnimation();
    }

    private static void showPlanetsPopup(ImageView currentCard, List<String> planetChosenBy) {
        Popup.newPlanetsCardPopup(currentCard.getImage(), planetChosenBy).show();

        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(_ -> {
            isAnimationPlaying = false;
            playNextAnimation();
        });
        pause.play();
    }

    public static void queueCardDecisionPopup(ImageView currentCard) {
        animationQueue.offer(() -> showCardDecisionPopup(currentCard));
        playNextAnimation();
    }

    private static void showCardDecisionPopup(ImageView currentCard) {
        Popup.newCardDecisionPopup(currentCard.getImage()).show();

        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(_ -> {
            isAnimationPlaying = false;
            playNextAnimation();
        });
        pause.play();
    }

    public static ParallelTransition getTileRemovalAnimation(double dx, double dy, Node node) {
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) distance = 1;
        double moveFactor = 100;
        double translateX = (dx / distance) * moveFactor;
        double translateY = (dy / distance) * moveFactor;

        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        RotateTransition rotate = new RotateTransition(Duration.millis(500), node);
        rotate.setByAngle(180);

        TranslateTransition translate = new TranslateTransition(Duration.millis(500), node);
        translate.setByX(translateX);
        translate.setByY(translateY);

        return new ParallelTransition(fade, rotate, translate);
    }

    public static ImageView getFaceDownCard(Integer cardLevel) {
        String path = "/textures/cards/card_level_" + cardLevel + "_back.png";
        javafx.scene.image.Image cardImage = new Image(Objects.requireNonNull(FlightAnim.class.getResourceAsStream(path)));
        ImageView card = new ImageView(cardImage);
        card.setFitWidth(166);
        card.setFitHeight(254);
        card.setPreserveRatio(true);
        card.setRotate(Math.random() * 10 - 5);
        card.setTranslateX(Math.random() * 10 - 5);
        card.setTranslateY(Math.random() * 10 - 5);
        setShadowToCard(card);
        card.setCache(true);
        return card;
    }

    public static void generatePlayerPawns(Set<String> playerNames, StackPane gameBoardPane) {
        for (String name : playerNames) {
            StackPane pawn = ClientSession.getPlayerProfilePicture(name, 35);
            playerPawns.put(name, pawn);
            gameBoardPane.getChildren().add(pawn);
        }
    }

    public static void generateGameBoardSlots(BoardType boardType) {
        boardSlotCoordinates.clear();

        try (InputStream is = FlightAnim.class.getResourceAsStream("/public/slot_positions.json")) {
            if (is == null) throw new IOException("JSON not found!");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);

            String key = boardType.toString();
            JsonNode boardNode = root.get(key);

            for (Iterator<String> it = boardNode.fieldNames(); it.hasNext(); ) {
                String index = it.next();
                JsonNode pos = boardNode.get(index);
                int x = pos.get("x").asInt();
                int y = pos.get("y").asInt();
                boardSlotCoordinates.put(Integer.parseInt(index), new Point(x, y));
            }

        } catch (IOException e) {
            Logger.logError("Error reading json:" + e.getMessage());
        }
    }
}
