package it.polimi.galaxy_trucker.Client.Controller.Scene1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Client.*;
import it.polimi.galaxy_trucker.Common.LobbyJoinResponse;
import it.polimi.galaxy_trucker.Common.PlayerInfo;
import it.polimi.galaxy_trucker.Server.Model.SceneSwitch;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Random;

public class Scene1Controller implements VirtualServerAware {

    @FXML
    private AnchorPane scene1AnchorPane;

    @FXML
    private TextField usernameTextField;

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
    private static final int MAX_ACCESSORIES = 9;

    private static int bodyIndex = 0;
    private static int accessoryIndex = 0;


    @FXML
    public void initialize() {

        randomizeSkin();

        leftBodyBtn.setOnAction(_ -> updateBody(-1));
        rightBodyBtn.setOnAction(_ -> updateBody(1));
        leftAccessoryBtn.setOnAction(_ -> updateAccessory(-1));
        rightAccessoryBtn.setOnAction(_ -> updateAccessory(1));
        randomizeButton.setOnAction(_ -> randomizeSkin());
        playButton.setOnAction(_ -> validateAndJoin());
        scene1AnchorPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                playButton.fire();
            }
        });
    }


    private Stage stage;
    private VirtualServer virtualServer;

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
    public void setVirtualServer(VirtualServer virtualServer) throws IOException {
        this.virtualServer = virtualServer;

        this.virtualServer.setUpdateUICallBack(new UpdateUICallBack() {
            @Override
            public void process(String cmd) throws JsonProcessingException {

                ObjectMapper mapper = new ObjectMapper();
                String type = mapper.readTree(cmd).get("type").asText();
                if (type.equals("lobbyJoinResponse")) {
                    LobbyJoinResponse joinResponse = mapper.readValue(cmd, LobbyJoinResponse.class);

                    if (joinResponse.getJoinStatus() == LobbyJoinResponse.JoinStatus.SUCCESS) {
                        VirtualServer.getInstance().setUpdateUICallBack(null);
                        Platform.runLater(() -> SceneSwitch.switchScene("lobby_scene.fxml"));
                        return;
                    }
                }
                safeUpdateView(cmd);
            }

            @Override
            public void disconnect() { }
        });
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
            showError(errorServerPort, LanguageManager.get("error.invalid_connection"));
            return;
        }
        this.virtualServer.sendCmd(playerInfo);

        ClientSession.setUsername(username);
        ClientSession.setProfilePicIds(profilePicIds);
    }

    private void updateView(String cmd) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        String type = mapper.readTree(cmd).get("type").asText();

        switch (type) {
            case "lobbyJoinResponse":
                LobbyJoinResponse joinResponse = mapper.readValue(cmd, LobbyJoinResponse.class);
                switch (joinResponse.getJoinStatus()) {
                    case SUCCESS -> throw new IllegalStateException("Response should not be successful.");
                    case LOBBY_FULL -> showError(errorUsername, LanguageManager.get("error.full_lobby"));
                    case NAME_TAKEN -> {
                        String errorMsg = MessageFormat.format(LanguageManager.get("error.username_used"), ClientSession.getUsername().toLowerCase());
                        showError(errorUsername, errorMsg);
                    }
                    case GAME_STARTED -> showError(errorUsername, LanguageManager.get("error.game_started"));
                }
                break;
        }
    }
}

