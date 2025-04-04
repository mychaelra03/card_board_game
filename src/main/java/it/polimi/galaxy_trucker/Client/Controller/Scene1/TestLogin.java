package it.polimi.galaxy_trucker.Client.Controller.Scene1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TestLogin {

    @FXML private ImageView gameTitle;
    @FXML private ImageView backgroundView;
    @FXML private ImageView bodyView;
    @FXML private ImageView accessoryView;

    @FXML private Button leftBodyBtn, rightBodyBtn;
    @FXML private Button leftAccessoryBtn, rightAccessoryBtn;
    @FXML private Button playButton;

    @FXML private TextField nameField;
    @FXML private Label errorLabel;

    private int bodyIndex = 0;
    private int accessoryIndex = 0;
    private static final int MAX_BODIES = 9;
    private static final int MAX_ACCESSORIES = 8;

    @FXML
    public void initialize() {
        gameTitle.setImage(loadImage("/textures/title.png"));
        //backgroundView.setImage(loadImage("/textures/characters/bg_blue.png"));
        bodyView.setImage(loadBodyImage(bodyIndex));
        accessoryView.setImage(loadAccessoryImage(accessoryIndex));

        leftBodyBtn.setOnAction(e -> updateBody(-1));
        rightBodyBtn.setOnAction(e -> updateBody(1));
        leftAccessoryBtn.setOnAction(e -> updateAccessory(-1));
        rightAccessoryBtn.setOnAction(e -> updateAccessory(1));
        playButton.setOnAction(e -> validateName());
    }

    private Image loadImage(String path) {
        return new Image(getClass().getResourceAsStream(path));
    }

    private Image loadBodyImage(int index) {
        return loadImage("/textures/characters/character_" + index + ".png");
    }

    private Image loadAccessoryImage(int index) {
        return loadImage("/textures/characters/hand_" + index + ".png");
    }

    private void updateBody(int change) {
        bodyIndex = (bodyIndex + change + MAX_BODIES) % MAX_BODIES;
        bodyView.setImage(loadBodyImage(bodyIndex));
    }

    private void updateAccessory(int change) {
        accessoryIndex = (accessoryIndex + change + MAX_ACCESSORIES) % MAX_ACCESSORIES;
        accessoryView.setImage(loadAccessoryImage(accessoryIndex));
    }

    private void validateName() {
        String name = nameField.getText().trim();
        if (name.isEmpty() || name.length() > 12) {
            errorLabel.setText("Name must be between 1 and 12 characters.");
            errorLabel.setVisible(true);
        } else if(true) { // check for duplicate names.
            errorLabel.setText("There is already a player named \""+ name +"\" in the lobby.");
            errorLabel.setVisible(true);
        } else {
            errorLabel.setVisible(false);
            loadGameScene();
        }
    }

    private void loadGameScene() {
        Stage stage = (Stage) playButton.getScene().getWindow();
        Label welcomeLabel = new Label("Welcome, " + nameField.getText() + "!");
        Scene nextScene = new Scene(new StackPane(welcomeLabel), 400, 300);
        stage.setScene(nextScene);
    }
}
