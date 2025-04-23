package it.polimi.galaxy_trucker.Client.Controller.Scene1;
import it.polimi.galaxy_trucker.Client.ClientSession;
import it.polimi.galaxy_trucker.Client.VirtualServer;
import it.polimi.galaxy_trucker.Client.VirtualServerAware;
import it.polimi.galaxy_trucker.Common.ChatMessage;
import it.polimi.galaxy_trucker.Common.PlayerInfo;
import it.polimi.galaxy_trucker.Server.Model.SceneSwitch;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class Scene1Controller implements VirtualServerAware {

    @FXML
    private AnchorPane scene1AnchorPane;

    @FXML
    private TextField usernameTextField;

    @FXML
    private ImageView myImageView;

    @FXML private ImageView bodyView;
    @FXML private ImageView accessoryView;
    @FXML private ImageView backgroundImage;

    @FXML private Button leftBodyBtn, rightBodyBtn;
    @FXML private Button leftAccessoryBtn, rightAccessoryBtn;
    @FXML private Button playButton;

    @FXML private TextField nameField;
    @FXML private Label errorLabel;

    private static int bodyIndex = 0;
    private static int accessoryIndex = 0;
    private static final int MAX_BODIES = 9;
    private static final int MAX_ACCESSORIES = 8;



    @FXML
    public void initialize() {

        System.out.println(getClass().getResource("/textures/background.png"));

        bodyView.setImage(loadBodyImage(bodyIndex));
        accessoryView.setImage(loadAccessoryImage(accessoryIndex));

        leftBodyBtn.setOnAction(e -> updateBody(-1));
        rightBodyBtn.setOnAction(e -> updateBody(1));
        leftAccessoryBtn.setOnAction(e -> updateAccessory(-1));
        rightAccessoryBtn.setOnAction(e -> updateAccessory(1));
        playButton.setOnAction(e -> {
            try {
                validateName();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        scene1AnchorPane.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                try {
                    validateName();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }


    private Stage stage;
    private VirtualServer virtualServer;

    @Override
    public void setVirtualServer(VirtualServer virtualServer) {
        this.virtualServer = virtualServer;
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

    private void validateName() throws IOException {
        String name = usernameTextField.getText().trim();
        if (name.isEmpty() || name.length() > 12) {
            errorLabel.setText("Name must be between 1 and 12 characters.");
            errorLabel.setVisible(true);
        } else if(false) { // check for duplicate names.
            errorLabel.setText("There is already a player named \""+ name +"\" in the lobby.");
            errorLabel.setVisible(true);
        } else {
            errorLabel.setVisible(false);
            String username = usernameTextField.getText();

            System.out.println(bodyIndex + " " + accessoryIndex + " " + username);

            int[] profilePicIds = new int[2];
            profilePicIds[0] = bodyIndex;
            profilePicIds[1] = accessoryIndex;

            PlayerInfo playerInfo = new PlayerInfo(username,profilePicIds,null);

            this.virtualServer.sendCmd(playerInfo);
            ClientSession.setUsername(username);
            ClientSession.setProfilePicIds(profilePicIds);
            SceneSwitch sceneSwitch = new SceneSwitch();
            sceneSwitch.switchScene("/it/polimi/galaxy_trucker/View/Untitled.fxml");
        }
    }

//
//    @FXML
//    private void onEnterKeyPressed(KeyEvent event) throws IOException {
//        if(event.getCode() == KeyCode.ENTER) {
//            validateName();
//        }
//    }

}

