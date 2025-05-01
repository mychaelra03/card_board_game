package it.polimi.galaxy_trucker.Client.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Client.*;
import it.polimi.galaxy_trucker.Common.*;
import it.polimi.galaxy_trucker.GameModel;
import it.polimi.galaxy_trucker.Server.Model.SceneSwitch;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.text.MessageFormat;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class PlayerCounterSceneControllerTest implements VirtualServerAware {

    @FXML private Label playerCounter;
    @FXML private Label readyCounter;

    @FXML private ImageView boardImage;

    @FXML private VBox playerList;

    @FXML private Button prevMapButton;
    @FXML private Button nextMapButton;

    @FXML private Button readyButton;

    @FXML private TextField chatTextField;

    @FXML private ScrollPane chatScrollPane;

    @FXML private VBox chatVbox;

    private static final double PROFILE_PIC_SIZE = 50;

    private VirtualServer virtualServer;

    private UpdateUICallBack updateUICallBack;

    @FXML
    public void initialize() {
        chatVbox.setPadding(new Insets(10, 5, 10, 5));
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setFitToHeight(false);

        chatScrollPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        chatVbox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

        prevMapButton.setOnAction(_ -> {
            cooldownButton(prevMapButton, .1);
            this.virtualServer.sendCmd(new BoardSelectMessage(BoardSelectMessage.Action.PREVIOUS));
        });
        nextMapButton.setOnAction(_ -> {
            cooldownButton(nextMapButton, .1);
            this.virtualServer.sendCmd(new BoardSelectMessage(BoardSelectMessage.Action.NEXT));
        });
        readyButton.setOnAction(_ -> {
            cooldownButton(readyButton, .2);
            this.virtualServer.sendCmd(new ReadyToggleMessage());
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
                if (type.equals("gameState")) {
                    GameStateMessage gameStateMessage = mapper.readValue(cmd, GameStateMessage.class);

                    if (gameStateMessage.getGameState() != GameModel.GameState.BUILDING_SHIPS) throw new IllegalStateException("Invalid game state");
                    Platform.runLater(() -> SceneSwitch.switchScene("ship_building_scene.fxml"));
                    return;
                }
                safeUpdateView(cmd);
            }

            @Override
            public void disconnect() {
                Platform.runLater(() -> {
                    SceneSwitch.switchScene("login_scene.fxml");
                });
            }
        });
    }

    private void cooldownButton(Button button, double cooldown) {
        button.setDisable(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(cooldown));
        pause.setOnFinished(event -> button.setDisable(false));
        pause.play();
    }

    private void safeUpdateView(String cmd) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    updateView(cmd);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
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
                    if (playersInfo.size() > currentCount) {
                        msg.setText(MessageFormat.format(LanguageManager.get("label.player_join"), playersInfoMessage.getUsername()));
                        msg.setTextFill(Color.GREEN);
                    } else {
                        msg.setText(MessageFormat.format(LanguageManager.get("label.player_left"), playersInfoMessage.getUsername()));
                        msg.setTextFill(Color.RED);
                    }
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
                String path = switch (boardTypeMessage.getBoardType()) {
                    case LEARNING, LEVEL_ONE -> "/textures/cardboards/showcase_1.png";
                    case LEVEL_TWO -> "/textures/cardboards/showcase_2.png";
                };
                Image board = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
                boardImage.setImage(board);
            }

            case "chatMessage" -> {

                ChatMessage chatMessage = mapper.readValue(cmd, ChatMessage.class);

                Label sender = new Label(chatMessage.getSender() + ": ");
                sender.setStyle("-fx-font-weight: bold");
                sender.setMinWidth(Region.USE_PREF_SIZE);
                sender.setTextFill(Paint.valueOf(chatMessage.getColor()));
                Label message = new Label(chatMessage.getMessage());
                message.setWrapText(true);
                message.setMinHeight(Region.USE_PREF_SIZE);
                HBox messageBox = new HBox(sender, message);
                messageBox.setFillHeight(true);
                messageBox.setAlignment(Pos.TOP_LEFT);
                if (chatMessage.getSender().equals(ClientSession.getUsername())) {
                    sender.setText(LanguageManager.get("text.you") + ": ");
                    message.setTextFill(Color.BLACK);
                    sender.setTextFill(Color.BLACK);
                }
                chatVbox.getChildren().add(messageBox);

                Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
            }

            case "playerKicked" -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Disconnected");
                alert.setHeaderText("Connection Lost");
                alert.setContentText("You have been kicked from the game.");
                alert.showAndWait();
            }

            default -> throw new IllegalArgumentException("Unknown command type: " + type);
        }
    }

    private void updatePlayerList(List<PlayerInfo> playersInfo, PlayerInfo thisPlayer) {
        for (PlayerInfo playerInfo : playersInfo) {
            boolean isCurrentUser = playerInfo.getUsername().equals(thisPlayer.getUsername());

            ImageView profilePicBody = new ImageView();
            ImageView profilePicAcc = new ImageView();
            Image bodyImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/characters/character_" + playerInfo.getProfilePicIds()[0] + ".png")));
            Image accessoryImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/characters/hand_" + playerInfo.getProfilePicIds()[1] + ".png")));

            profilePicBody.setImage(bodyImage);
            profilePicBody.setFitHeight(PROFILE_PIC_SIZE);
            profilePicBody.setFitWidth(PROFILE_PIC_SIZE);

            profilePicAcc.setImage(accessoryImage);
            profilePicAcc.setFitHeight(PROFILE_PIC_SIZE);
            profilePicAcc.setFitWidth(PROFILE_PIC_SIZE);

            double radius = PROFILE_PIC_SIZE / 2;
            Circle profilePicBackground = new Circle(radius);

            profilePicBackground.setFill(Paint.valueOf(playerInfo.getColor()));
            profilePicBackground.setStroke(Color.BLACK);
            profilePicBackground.setStrokeWidth(1);

            // Add a circle clip to the first avatar image to make it circular
            Circle bodyClip = new Circle(radius);
            bodyClip.setCenterX(profilePicBody.getFitWidth() / 2); // Center the clip within the ImageView
            bodyClip.setCenterY(profilePicBody.getFitHeight() / 2);
            profilePicBody.setClip(bodyClip);

            Circle accClip = new Circle(radius);
            accClip.setCenterX(profilePicAcc.getFitWidth() / 2); // Center the clip within the ImageView
            accClip.setCenterY(profilePicAcc.getFitHeight() / 2);
            profilePicAcc.setClip(accClip);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            StackPane profilePicture = new StackPane(profilePicBackground, profilePicBody, profilePicAcc);


            Text username = new Text(playerInfo.getUsername());
            username.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            TextFlow usernameText = new TextFlow(username);
            usernameText.setPrefHeight(Region.USE_COMPUTED_SIZE);
            usernameText.setMaxHeight(Region.USE_PREF_SIZE);
            usernameText.setTextAlignment(TextAlignment.LEFT);
            if (isCurrentUser) {
                Text self = new Text(" (" + LanguageManager.get("text.you") + ")");
                self.setStyle("-fx-font-size: 14px;");
                self.setFill(Color.GRAY);
                usernameText.getChildren().add(self);
            }

            Label readyLabel = new Label();
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
                HBox.setMargin(adminCrown, new Insets(0, 20, 0, 0));
                playerBox.getChildren().add(adminCrown);
            }

            if (thisPlayer.isAdmin()) {
                if (!playerInfo.isAdmin()) {
                    ImageView dotsMenu = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/icons/dotsMenuIcon.png"))));
                    dotsMenu.setFitHeight(15);
                    dotsMenu.setFitWidth(4);
                    dotsMenu.setCursor(Cursor.HAND);

                    dotsMenu.setOnMouseClicked(event -> {

                        ContextMenu contextMenu = new ContextMenu();

                        MenuItem kick = new MenuItem(LanguageManager.get("menu_item.kick"));
                        MenuItem promote = new MenuItem(LanguageManager.get("menu_item.promote"));

                        kick.setOnAction(e -> {
                            //TODO
                            playerInfo.setType("kickPlayer");
                            this.virtualServer.sendCmd(playerInfo);
                        });

                        promote.setOnAction(e -> {
                            //TODO
                            playerInfo.setType("promotePlayer");
                            this.virtualServer.sendCmd(playerInfo);
                        });

                        contextMenu.getItems().addAll(kick, promote);

                        contextMenu.show(dotsMenu, event.getScreenX(), event.getScreenY());
                    });

                    HBox.setMargin(dotsMenu, new Insets(0, 27, 0, 0));
                    playerBox.getChildren().add(dotsMenu);
                }
            }

            playerList.setSpacing(10);
            playerList.setPadding(new Insets(10, 5, 10, 5));

            playerList.getChildren().add(playerBox);
        }
    }

    @FXML
    private void onEnterKeyPressed(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {

            String message = chatTextField.getText().trim();
            chatTextField.setText("");
            if(message.isEmpty()) return;
            ChatMessage chatMessage = new ChatMessage(message);
            this.virtualServer.sendCmd(chatMessage);
        }
    }

    // TODO:  - Admin (first player) ------->  DONE
    //        - Admin associated privileges :
    //              + kick players (icon 3 dots :  -------> DONE
    //                                          - pass Admin privileges
    //                                          - kick player ------->  DONE
    //                              )
    //              + choose map
    //              + start game on players Ready
    //
}
