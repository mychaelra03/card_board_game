package it.polimi.galaxy_trucker.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.BoardType;
import it.polimi.galaxy_trucker.client.*;
import it.polimi.galaxy_trucker.client.popups.PendingPopup;
import it.polimi.galaxy_trucker.client.popups.Popup;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.client.SceneSwitch;
import it.polimi.galaxy_trucker.common.Logger;
import it.polimi.galaxy_trucker.common.messages.*;
import it.polimi.galaxy_trucker.common.messages.lobby.BoardSelectMessage;
import it.polimi.galaxy_trucker.common.messages.lobby.ChatMessage;
import it.polimi.galaxy_trucker.common.messages.lobby.ReadyToggleMessage;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;


public class LobbyController implements VirtualServerAware {

    @FXML private StackPane rootPane;
    @FXML private AnchorPane mainContentPane;

    @FXML private Label playerCounter;
    @FXML private Label readyCounter;

    @FXML private ImageView boardImage;
    @FXML private Label boardName;

    @FXML private VBox playerList;

    @FXML private Button settingsButton;

    @FXML private Button prevMapButton;
    @FXML private Button nextMapButton;

    @FXML private Button readyButton;

    @FXML private TextField chatTextField;

    @FXML private ScrollPane chatScrollPane;

    @FXML private VBox chatVbox;

    private final VirtualServer virtualServer = VirtualServer.getInstance();

    @FXML
    public void initialize() {
        chatVbox.setPadding(new Insets(10, 5, 10, 5));
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setFitToHeight(false);

        chatScrollPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        chatVbox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        settingsButton.setOnAction(event -> Popup.newSettingsPopup().show(rootPane, mainContentPane));

        prevMapButton.setOnAction(_ -> {
            cooldownButton(prevMapButton, .1);
            this.virtualServer.sendMessage(new BoardSelectMessage(BoardSelectMessage.Action.PREVIOUS));
        });
        nextMapButton.setOnAction(_ -> {
            cooldownButton(nextMapButton, .1);
            this.virtualServer.sendMessage(new BoardSelectMessage(BoardSelectMessage.Action.NEXT));
        });
        readyButton.setOnAction(_ -> {
            cooldownButton(readyButton, .2);
            this.virtualServer.sendMessage(new ReadyToggleMessage());
        });
    }

    @Override
    public void updateCallBack() {
        virtualServer.setCallBack(cmd -> {
            ObjectMapper mapper = new ObjectMapper();
            String type = mapper.readTree(cmd).get("type").asText();
            switch (type) {
                case "kicked" -> {
                    //VirtualServer.getInstance().setCallBack(null);
                    PendingPopup.set(Popup.newAlertPopup("title.disconnected", "description.kicked"));
                    virtualServer.close();
                    //Platform.runLater(() -> SceneSwitch.switchScene("LoginScene"));
                    return;
                }
                case "gameState" -> {
                    GameStateMessage gameStateMessage = mapper.readValue(cmd, GameStateMessage.class);

                    if (gameStateMessage.getGameState() != GameModel.GameState.BUILDING_SHIPS) throw new IllegalStateException("Invalid game state");
                    virtualServer.setCallBack(null);
                    Platform.runLater(() -> SceneSwitch.switchScene("ShipBuildingScene"));
                    return;
                }
            }
            safeUpdateView(cmd);
        });
    }

    private void cooldownButton(Button button, double cooldown) {
        button.setDisable(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(cooldown));
        pause.setOnFinished(event -> button.setDisable(false));
        pause.play();
    }

    private void safeUpdateView(String cmd) {
        Platform.runLater(() -> {
            try {
                updateView(cmd);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void updateView(String cmd) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(cmd);

        String type = node.get("type").asText();

        switch (type) {
            case "playersInfo" -> {

                PlayersInfoMessage playersInfoMessage = mapper.readValue(cmd, PlayersInfoMessage.class);
                List<PlayerInfo> playersInfo = playersInfoMessage.getPlayers();

                ClientSession.updatePlayers(playersInfo);

                playerList.getChildren().clear();

                PlayerInfo thisPlayer = null;
                for (PlayerInfo pi : playersInfo) {
                    if (pi.getUsername().equals(ClientSession.getUsername())) {
                        thisPlayer = pi;
                        break;
                    }
                }
                if(thisPlayer == null) throw new RuntimeException("This client is not in the player list.");

                int currentCount = Integer.parseInt(playerCounter.getText().split(" / ")[0]);
                if (playersInfo.size() != currentCount) {
                    Label msg = new Label();
                    msg.setStyle("-fx-font-weight: bold; -fx-background-radius: 5; -fx-font-size: 14px;");

                    if (playersInfo.size() > currentCount) {
                        msg.setText(MessageFormat.format(LanguageManager.get("label.player_join"), playersInfoMessage.getUsername()));
                        msg.setTextFill(Color.GREEN);
                        msg.setStyle(msg.getStyle() + "-fx-background-color: rgba(0,128,0,0.1);");
                    } else {
                        msg.setText(MessageFormat.format(LanguageManager.get("label.player_left"), playersInfoMessage.getUsername()));
                        msg.setTextFill(Color.RED);
                        msg.setStyle(msg.getStyle() + "-fx-background-color: rgba(255,0,0,0.1);");
                    }
                    msg.setPadding(new Insets(2, 0, 2, 0));
                    msg.setPrefWidth(chatScrollPane.getWidth());
                    msg.setAlignment(Pos.CENTER);

                    chatVbox.getChildren().add(msg);

                    playerCounter.setText(playersInfo.size() + " / 4");
                }

                boolean isAdmin = thisPlayer.isAdmin();
                prevMapButton.setVisible(isAdmin);
                nextMapButton.setVisible(isAdmin);

                int readyCount = (int) playersInfo.stream().filter(PlayerInfo::isReady).count();
                int readyRequired = Math.max(2, playersInfo.size());
                readyCounter.setText(readyCount + " / " + readyRequired);

                readyButton.setText(thisPlayer.isReady()? LanguageManager.get("button.cancel") : LanguageManager.get("button.ready"));

                updatePlayerList(playersInfo, thisPlayer);
            }

            case "boardType" -> {

                BoardTypeMessage boardTypeMessage = mapper.readValue(cmd, BoardTypeMessage.class);
                BoardType boardType = boardTypeMessage.getBoardType();
                String path = switch (boardType) {
                    case LEARNING, LEVEL_ONE -> "/textures/cardboards/showcase_1.png";
                    case LEVEL_TWO -> "/textures/cardboards/showcase_2.png";
                };
                Image board = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
                boardImage.setImage(board);
                boardName.setText(LanguageManager.get("label." + boardType));
            }

            case "chatMessage" -> {

                ChatMessage chatMessage = mapper.readValue(cmd, ChatMessage.class);

                Label sender = new Label(chatMessage.getSender() + ":");
                sender.setStyle("-fx-font-size: 14; -fx-font-weight: bold");
                sender.setMinWidth(Region.USE_PREF_SIZE);
                sender.setTextFill(Paint.valueOf(chatMessage.getColor()));
                Label message = new Label(chatMessage.getMessage());
                message.setWrapText(true);
                message.setMinHeight(Region.USE_PREF_SIZE);
                message.setTextFill(Color.BLACK);
                message.setStyle("-fx-font-size: 14;");

                StackPane profilePic = ClientSession.getPlayerProfilePicture(chatMessage.getSender(), 20);

                HBox messageBox = new HBox(profilePic, sender, message);
                messageBox.setFillHeight(true);
                messageBox.setAlignment(Pos.TOP_LEFT);
                messageBox.setSpacing(4);
                if (chatMessage.getSender().equals(ClientSession.getUsername())) {
                    sender.setText(LanguageManager.get("text.you") + ":");
                }
                chatVbox.getChildren().add(messageBox);

                Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
            }

            default -> Logger.logError("Unrecognized command: " + type);
        }
    }

    private void updatePlayerList(List<PlayerInfo> playersInfo, PlayerInfo thisPlayer) {

        for (PlayerInfo playerInfo : playersInfo) {
            boolean isCurrentUser = playerInfo.getUsername().equals(thisPlayer.getUsername());

            StackPane profilePicture = ClientSession.getPlayerProfilePicture(playerInfo.getUsername(), 65);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Text username = new Text(playerInfo.getUsername());
            username.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
            username.setFill(Color.GOLD);
            TextFlow usernameText = new TextFlow(username);
            usernameText.setPrefHeight(Region.USE_COMPUTED_SIZE);
            usernameText.setMaxHeight(Region.USE_PREF_SIZE);
            usernameText.setTextAlignment(TextAlignment.LEFT);
            if (isCurrentUser) {
                Text self = new Text(" (" + LanguageManager.get("text.you") + ")");
                self.setStyle("-fx-font-size: 18px;");
                self.setFill(Color.GRAY);
                usernameText.getChildren().add(self);
            }

            Label readyLabel = new Label();
            readyLabel.setStyle("-fx-font-size: 16px");
            if (playerInfo.isReady()) {
                readyLabel.setText(LanguageManager.get("label.player_ready"));
                readyLabel.setTextFill(Color.GREEN);
            } else {
                readyLabel.setText(LanguageManager.get("label.player_not_ready"));
                readyLabel.setTextFill(Color.RED);
            }

            VBox nameAndStatus = new VBox(usernameText, readyLabel);
            nameAndStatus.setAlignment(Pos.CENTER_LEFT);

            HBox playerBox = new HBox(profilePicture, nameAndStatus, spacer);
            playerBox.setAlignment(Pos.CENTER_LEFT);
            playerBox.setSpacing(10);

            if (playerInfo.isAdmin()) {
                ImageView adminCrown = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/icons/crownIcon.png"))));
                adminCrown.setFitHeight(15);
                adminCrown.setFitWidth(15);
                HBox.setMargin(adminCrown, new Insets(0, 18, 0, 0));
                playerBox.getChildren().add(adminCrown);
            }

            if (thisPlayer.isAdmin()) {
                if (!playerInfo.isAdmin()) {

                    Button dotsButton = getDotsButton(playerInfo);

                    playerBox.getChildren().add(dotsButton);
                }
            }

            playerList.getChildren().add(playerBox);
        }
    }

    private Button getDotsButton(PlayerInfo playerInfo) {

        ImageView dotsImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/icons/dotsMenuIcon.png"))));
        dotsImage.setFitHeight(15);
        dotsImage.setFitWidth(15);
        dotsImage.setPreserveRatio(true);

        Button dotsButton = new Button("", dotsImage);
        dotsButton.setPrefSize(18, 18);
        dotsButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        dotsButton.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        dotsButton.setFocusTraversable(false);
        dotsButton.setCursor(Cursor.HAND);
        dotsButton.setStyle("-fx-background-color: transparent");

        dotsButton.setOnAction(_ -> {

            ContextMenu contextMenu = new ContextMenu();

            MenuItem promote = new MenuItem(LanguageManager.get("menu_item.promote"));
            MenuItem kick = new MenuItem(LanguageManager.get("menu_item.kick"));
            promote.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");

            promote.setOnAction(_ -> {
                playerInfo.setType("promotePlayer");
                this.virtualServer.sendMessage(playerInfo);
            });
            kick.setOnAction(_ -> {
                playerInfo.setType("kickPlayer");
                this.virtualServer.sendMessage(playerInfo);
            });

            contextMenu.getItems().addAll(promote, kick);

            contextMenu.show(dotsButton, Side.RIGHT, 0, 0);
        });
        HBox.setMargin(dotsButton, new Insets(0, 16, 0, 0));
        return dotsButton;
    }

    @FXML
    private void onEnterKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) sendChatMessage();
    }

    public void sendChatMessage() {
        String message = chatTextField.getText().trim();
        if (message.isEmpty()) return;

        chatTextField.clear();
        virtualServer.sendMessage(new ChatMessage(message));
    }
}
