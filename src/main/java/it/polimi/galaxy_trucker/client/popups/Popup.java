package it.polimi.galaxy_trucker.client.popups;

import it.polimi.galaxy_trucker.client.LanguageManager;
import it.polimi.galaxy_trucker.client.SceneSwitch;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.animation.*;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Popup {

    private final Node content;
    private final Region overlay;

    private final Map<Node, Effect> originalEffects = new HashMap<>();

    private Object controller = null;
    private boolean isClosing = false;

    public Popup(Node content, double width, double height) {

        StackPane popup = new StackPane(content);
        popup.setPrefSize(width, height);
        popup.setMaxSize(width, height);
        popup.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        StackPane.setAlignment(popup, Pos.CENTER);
        this.content = popup;

        this.overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");
    }

    private Popup(String fxmlFile, Object... args) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/popups/" + fxmlFile + ".fxml"), LanguageManager.getBundle());
            Node content = loader.load();

            controller = loader.getController();
            if (controller instanceof PopupAware controllerAware) {
                controllerAware.bindPopup(this);
                controllerAware.initialize(args);
            }

            this.content = content;

            this.overlay = new Region();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlFile, e);
        }
    }

//    public void show(StackPane rootPane, AnchorPane mainContentPane) {
//        this.rootPane = rootPane;
//        this.mainContentPane = mainContentPane;
//
//        ParallelTransition openingAnimation = getOpeningAnimation();
//
//        rootPane.getChildren().addAll(overlay, content);
//        openingAnimation.play();
//
//        originalEffect = mainContentPane.getEffect();
//        BoxBlur blur = new BoxBlur(5, 5, 5);
//        if (originalEffect != null) blur.setInput(originalEffect);
//        mainContentPane.setEffect(blur);
//        mainContentPane.setMouseTransparent(true);
//
//        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
//        overlay.setOnMouseClicked(_ -> close());
//
//        Scene scene = rootPane.getScene();
//        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
//            if (event.getCode() == KeyCode.ESCAPE) {
//                close();
//            }
//        });
//    }

    public void show() {
        Scene scene = SceneSwitch.getStage().getScene();
        if (!(scene.getRoot() instanceof StackPane rootPane)) {
            throw new IllegalStateException("Popup requires scene root to be a StackPane");
        }

        ParallelTransition openingAnimation = getOpeningAnimation();

        rootPane.getChildren().addAll(overlay, content);
        openingAnimation.play();

        applyBlur(rootPane);

        overlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
        overlay.setOnMouseClicked(_ -> close());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                close();
            }
        });
    }

    private void close() {
        if(isClosing) return;
        isClosing = true;

        if (controller instanceof PopupAware controllerAware) {
            controllerAware.onClose();
        }
        else playClosingAnimation();
    }

    public void playClosingAnimation() {
        StackPane rootPane = getRootPane();
        if(rootPane == null) return;

        ParallelTransition closingAnimation = getClosingAnimation();

        closingAnimation.setOnFinished(_ -> {
            rootPane.getChildren().removeAll(overlay, content);
            clearBlur();
        });

        closingAnimation.play();
    }


    public static Popup newSettingsPopup(boolean isMainMenu) {
        return new Popup("SettingsPopup", isMainMenu);
    }
    public static Popup newSettingsPopup() {
        return new Popup("SettingsPopup");
    }

    public static Popup newAlertPopup(String title, String description, String descriptionArgs) {
        return new Popup("AlertPopup", title, description, descriptionArgs);
    }
    public static Popup newAlertPopup(String title, String description) {
        return new Popup("AlertPopup", title, description);
    }

    public static Popup newCardsPopup(int[] levelOneCards, int[] levelTwoCards) {
        return new Popup("CardsPopup", levelOneCards, levelTwoCards);
    }
    public static Popup newCardsPopup(int[] levelOneCards) {
        return new Popup("CardsPopup", levelOneCards);
    }

    public static Popup newCountdownPopup(int duration) {
        return new Popup("CountdownPopup", duration);
    }

    public static Popup newConfirmationPopup(String prompt, Runnable action) {
        return new Popup("ConfirmationPopup", prompt, action);
    }


    private StackPane getRootPane() {
        Scene scene = SceneSwitch.getStage().getScene();
        if (!(scene.getRoot() instanceof StackPane rootPane)) {
            throw new IllegalStateException("Popup requires scene root to be a StackPane");
        }
        return rootPane;
    }

    private void applyBlur(StackPane rootPane) {
        for (Node node : rootPane.getChildren()) {
            if (node == content || node == overlay) continue;

            originalEffects.put(node, node.getEffect());

            BoxBlur blur = new BoxBlur(5, 5, 5);
            if (node.getEffect() != null) {
                blur.setInput(node.getEffect());
            }

            node.setEffect(blur);
            node.setMouseTransparent(true);
        }
    }

    private void clearBlur() {
        for (Node node : originalEffects.keySet()) {
            node.setEffect(originalEffects.get(node));
            node.setMouseTransparent(false);
        }
        originalEffects.clear();
    }

    private ParallelTransition getOpeningAnimation() {
        FadeTransition fadeContent = new FadeTransition(Duration.millis(200), content);
        fadeContent.setFromValue(0);
        fadeContent.setToValue(1);
        FadeTransition fadeOverlay = new FadeTransition(Duration.millis(200), overlay);
        fadeOverlay.setFromValue(0);
        fadeOverlay.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(200), content);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.0);
        scale.setToY(1.0);

        return new ParallelTransition(fadeContent, fadeOverlay, scale);
    }
    private ParallelTransition getClosingAnimation() {
        FadeTransition fadeContent = new FadeTransition(Duration.millis(150), content);
        fadeContent.setFromValue(1);
        fadeContent.setToValue(0);
        FadeTransition fadeOverlay = new FadeTransition(Duration.millis(150), overlay);
        fadeOverlay.setFromValue(1);
        fadeOverlay.setToValue(0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(150), content);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.8);
        scale.setToY(0.8);

        return new ParallelTransition(fadeContent, fadeOverlay, scale);
    }
}
