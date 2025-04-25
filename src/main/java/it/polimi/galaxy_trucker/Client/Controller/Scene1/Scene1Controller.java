package it.polimi.galaxy_trucker.Client.Controller.Scene1;
import it.polimi.galaxy_trucker.Client.ClientSession;
import it.polimi.galaxy_trucker.Client.LanguageManager;
import it.polimi.galaxy_trucker.Client.VirtualServer;
import it.polimi.galaxy_trucker.Client.VirtualServerAware;
import it.polimi.galaxy_trucker.Common.PlayerInfo;
import it.polimi.galaxy_trucker.Server.Model.SceneSwitch;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Random;

public class Scene1Controller implements VirtualServerAware {

    @FXML
    private AnchorPane scene1AnchorPane;

    @FXML
    private TextField usernameTextField;

    @FXML
    private ImageView myImageView;

    @FXML private ImageView bodyView;
    @FXML private ImageView accessoryView;

    @FXML private TextField serverIpTextField;
    @FXML private TextField serverPortTextField;
    @FXML private Label errorUsername;
    @FXML private Label errorServerIp;
    @FXML private Label errorServerPort;

    @FXML private Button leftBodyBtn, rightBodyBtn;
    @FXML private Button leftAccessoryBtn, rightAccessoryBtn;
    @FXML private Button randomizeButton, options;
    @FXML private Button playButton;

    private static final int MAX_BODIES = 9;
    private static final int MAX_ACCESSORIES = 10;

    private static int bodyIndex = 0;
    private static int accessoryIndex = 0;


    @FXML
    public void initialize() {

        System.out.println(getClass().getResource("/textures/background.png"));

        randomizeSkin();

        leftBodyBtn.setOnAction(e -> updateBody(-1));
        rightBodyBtn.setOnAction(e -> updateBody(1));
        leftAccessoryBtn.setOnAction(e -> updateAccessory(-1));
        rightAccessoryBtn.setOnAction(e -> updateAccessory(1));
        randomizeButton.setOnAction(e -> randomizeSkin());
        playButton.setOnAction(e -> validateAndJoin());
        scene1AnchorPane.setOnKeyPressed(e -> validateAndJoin());
    }


    private Stage stage;
    private VirtualServer virtualServer;

    @Override
    public void setVirtualServer(VirtualServer virtualServer) {
        this.virtualServer = virtualServer;
    }

    private Image loadImage(String path) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
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

    private void randomizeSkin() {
        Random random = new Random();
        bodyIndex = random.nextInt(MAX_BODIES);
        accessoryIndex = random.nextInt(MAX_ACCESSORIES);
        bodyView.setImage(loadBodyImage(bodyIndex));
        accessoryView.setImage(loadAccessoryImage(accessoryIndex));
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    private void validateAndJoin() {
        errorUsername.setVisible(false);
        errorServerIp.setVisible(false);
        errorServerPort.setVisible(false);
        String username = usernameTextField.getText().trim();
        if(username.isEmpty() || username.length() > 12) {
            showError(errorUsername, LanguageManager.get("error.username_length"));
            errorUsername.setVisible(true);
            return;
        }
        if(false) { // TODO: check for duplicate names.
            String errorMsg = MessageFormat.format(LanguageManager.get("error.username_used"), username);
            showError(errorUsername, errorMsg);
            return;
        }

        String hostName = serverIpTextField.getText().trim();
        int portNumber;
        try {
            portNumber = Integer.parseInt(serverPortTextField.getText());
        } catch (NumberFormatException e) {
            showError(errorServerPort, LanguageManager.get("error.port_type"));
            return;
        }
        if(portNumber < 0 || portNumber > 65535) {
            showError(errorServerPort, LanguageManager.get("error.port_range"));
            return;
        }

        //System.out.println(bodyIndex + " " + accessoryIndex + " " + username);

        int[] profilePicIds = new int[]{bodyIndex, accessoryIndex};

        PlayerInfo playerInfo = new PlayerInfo(username,profilePicIds,null,false);

        try {
            virtualServer.start(hostName, portNumber);
        } catch (IOException e) {
            errorServerPort.setText(LanguageManager.get("error.invalid_connection"));
            errorServerPort.setVisible(true);
            return;
        }
        this.virtualServer.sendCmd(playerInfo);

        ClientSession.setUsername(username);
        ClientSession.setProfilePicIds(profilePicIds);
        SceneSwitch.switchScene("/it/polimi/galaxy_trucker/View/Untitled.fxml");
    }

//
//    @FXML
//    private void onEnterKeyPressed(KeyEvent event) throws IOException {
//        if(event.getCode() == KeyCode.ENTER) {
//            validateName();
//        }
//    }

}

