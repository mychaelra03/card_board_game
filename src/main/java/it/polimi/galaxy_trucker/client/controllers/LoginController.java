package it.polimi.galaxy_trucker.client.controllers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.client.*;
import it.polimi.galaxy_trucker.client.popups.PendingPopup;
import it.polimi.galaxy_trucker.client.popups.Popup;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.login.LobbyJoinResponse;
import it.polimi.galaxy_trucker.common.messages.PlayerInfo;
import it.polimi.galaxy_trucker.client.SceneSwitch;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class LoginController implements VirtualServerAware {

    @FXML private StackPane rootPane;
    @FXML private AnchorPane mainContentPane;

    @FXML private TextField usernameTextField;

    @FXML private ImageView bodyView;
    @FXML private ImageView accessoryView;

    @FXML private TextField serverIpTextField;
    @FXML private TextField serverPortTextField;
    @FXML private Label errorUsername;
    @FXML private Label errorServerIp;
    @FXML private Label errorServerPort;

    @FXML private Button leftBodyBtn, rightBodyBtn;
    @FXML private Button leftAccessoryBtn, rightAccessoryBtn;
    @FXML private Button randomizeButton, settingsButton;
    @FXML private Button playButton;

    private static final int MAX_BODIES = 9;
    private static final int MAX_ACCESSORIES = 9;

    private static int bodyIndex = 0;
    private static int accessoryIndex = 0;


    @FXML
    public void initialize() {
        VirtualServer.getInstance().close();
        Platform.runLater(() -> PendingPopup.tryShow(rootPane, mainContentPane));

        if(ClientSession.getUsername() != null) {
            usernameTextField.setText(ClientSession.getUsername());
        }
        if(ClientSession.getProfilePicture() != null) {
            int[] profilePicture = ClientSession.getProfilePicture();
            updateBodyView(profilePicture[0]);
            updateAccessoryView(profilePicture[1]);
        } else randomizeSkin();

        leftBodyBtn.setOnAction(_ -> changeBody(-1));
        rightBodyBtn.setOnAction(_ -> changeBody(1));
        leftAccessoryBtn.setOnAction(_ -> changeAccessory(-1));
        rightAccessoryBtn.setOnAction(_ -> changeAccessory(1));
        randomizeButton.setOnAction(_ -> randomizeSkin());
        playButton.setOnAction(_ -> validateAndJoin());
        settingsButton.setOnAction(_ -> Popup.newSettingsPopup(true).show());
    }

    private final VirtualServer virtualServer = VirtualServer.getInstance();

    private void safeUpdateView(String cmd) {
        Platform.runLater(() -> {
            try {
                updateView(cmd);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void updateCallBack() {
        virtualServer.setCallBack(cmd -> {

            ObjectMapper mapper = new ObjectMapper();
            String type = mapper.readTree(cmd).get("type").asText();
            if (type.equals("lobbyJoinResponse")) {
                LobbyJoinResponse joinResponse = mapper.readValue(cmd, LobbyJoinResponse.class);

                if (joinResponse.getJoinStatus() == LobbyJoinResponse.JoinStatus.SUCCESS) {
                    VirtualServer.getInstance().setCallBack(null);
                    Platform.runLater(() -> SceneSwitch.switchScene("LobbyScene"));
                    return;
                }
            }
            safeUpdateView(cmd);
        });
    }

    private Image loadImage(String path) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    private void updateBodyView(int index) {
        bodyView.setImage(loadImage("/textures/characters/character_" + index + ".png"));
    }

    private void updateAccessoryView(int index) {
        accessoryView.setImage(loadImage("/textures/characters/hand_" + index + ".png"));
    }

    private void changeBody(int change) {
        bodyIndex = (bodyIndex + change + MAX_BODIES) % MAX_BODIES;
        updateBodyView(bodyIndex);
    }

    private void changeAccessory(int change) {
        accessoryIndex = (accessoryIndex + change + MAX_ACCESSORIES) % MAX_ACCESSORIES;
        updateAccessoryView(accessoryIndex);
    }

    private void randomizeSkin() {
        Random random = new Random();
        bodyIndex = random.nextInt(MAX_BODIES);
        accessoryIndex = random.nextInt(MAX_ACCESSORIES);
        updateBodyView(bodyIndex);
        updateAccessoryView(accessoryIndex);
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    private void cooldownPlayButton() {
        playButton.setDisable(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> playButton.setDisable(false));
        pause.play();
    }

    private void validateAndJoin() {
        errorUsername.setVisible(false);
        errorServerIp.setVisible(false);
        errorServerPort.setVisible(false);
        String username = usernameTextField.getText().trim();
        if(username.isEmpty() || username.length() > 12) {
            showError(errorUsername, LanguageManager.get("error.username_length"));
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

        int[] profilePicIds = new int[]{bodyIndex, accessoryIndex};
        PlayerInfo playerInfo = new PlayerInfo(username,profilePicIds,null,false,false);

        cooldownPlayButton();
        try {
            virtualServer.attemptConnection(hostName, portNumber);
        } catch (IOException e) {
            //showError(errorServerPort, LanguageManager.get("error.invalid_connection"));
            Popup.newAlertPopup("title.no_server_found", "description.no_server_found").show();
            return;
        }
        this.virtualServer.sendMessage(playerInfo);

        ClientSession.setUsername(username);
        ClientSession.setProfilePicture(profilePicIds);
    }

    private void updateView(String cmd) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        String type = mapper.readTree(cmd).get("type").asText();

        if (type.equals("lobbyJoinResponse")) {
            LobbyJoinResponse joinResponse = mapper.readValue(cmd, LobbyJoinResponse.class);

            switch (joinResponse.getJoinStatus()) {
                case SUCCESS -> throw new IllegalStateException("Response should not be successful.");
                case LOBBY_FULL -> Popup.newAlertPopup("title.unable_to_connect", "description.full_lobby").show();
                case NAME_TAKEN -> {
                    String usedName = ClientSession.getUsername().toLowerCase();
                    Popup.newAlertPopup("title.unable_to_connect", "description.username_used", usedName).show();
                }
                case GAME_STARTED -> Popup.newAlertPopup("title.unable_to_connect", "description.game_started").show();
            }
            PendingPopup.clear();
        } else {
            Logger.logError("Unrecognized command: " + type);
        }
    }
}

