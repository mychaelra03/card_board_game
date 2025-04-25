package it.polimi.galaxy_trucker.Client.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Client.*;
import it.polimi.galaxy_trucker.Common.ChatMessage;
import it.polimi.galaxy_trucker.Common.PlayerInfo;
import it.polimi.galaxy_trucker.Common.PlayersInfoMessage;
import it.polimi.galaxy_trucker.Server.Model.SceneSwitch;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.Cursor;
import javafx.scene.Node;
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

import java.text.MessageFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class PlayerCounterSceneControllerTest implements VirtualServerAware {

    @FXML private Label playerCounterLabel;

    @FXML private AnchorPane anchorPaneLobby;

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
            case "playersInfo":

                playerList.getChildren().clear();

                PlayersInfoMessage playersInfoMessage = mapper.readValue(cmd, PlayersInfoMessage.class);
                List<PlayerInfo> playersInfo = playersInfoMessage.getPlayers();


                boolean isCurrentUserAdmin = false;

                for(PlayerInfo p : playersInfo) {
                    if (p.getUsername().equals(ClientSession.getUsername())) {
                        isCurrentUserAdmin = p.isAdmin();
                        break;
                    }
                }

                if(playersInfo.size() > Integer.parseInt(playerCounterLabel.getText())) {

                    String joinMsg = MessageFormat.format(LanguageManager.get("label.player_join"), playersInfoMessage.getUsername());
                    Label playerEnteredLabel = new Label(joinMsg);
                    playerEnteredLabel.setTextFill(Color.GREEN);
                    playerEnteredLabel.setPrefWidth(chatScrollPane.getWidth());
                    playerEnteredLabel.setAlignment(Pos.CENTER);

                    chatVbox.getChildren().add(playerEnteredLabel);
                }
                else if(playersInfo.size() != Integer.parseInt(playerCounterLabel.getText())) {

                    String leftMsg = MessageFormat.format(LanguageManager.get("label.player_left"), playersInfoMessage.getUsername());
                    Label playerLeftLabel = new Label(leftMsg);
                    playerLeftLabel.setTextFill(Color.RED);
                    playerLeftLabel.setPrefWidth(chatScrollPane.getWidth());
                    playerLeftLabel.setAlignment(Pos.CENTER);

                    chatVbox.getChildren().add(playerLeftLabel);
                }

                playerCounterLabel.setText(String.valueOf(playersInfo.size()));


                for (PlayerInfo playerInfo : playersInfo) {

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

                    Text username = new Text(playerInfo.getUsername());
                    TextFlow usernameText = new TextFlow(username);
                    usernameText.setPrefHeight(Region.USE_COMPUTED_SIZE);
                    usernameText.setMaxHeight(Region.USE_PREF_SIZE);
                    usernameText.setTextAlignment(TextAlignment.LEFT);
                    if(playerInfo.getUsername().equals(ClientSession.getUsername())){
                        Text user = new Text(" (" + LanguageManager.get("text.you") + ")");
                        user.setFill(Color.GRAY);
                        usernameText.getChildren().add(user);
                    }

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

                    StackPane profilePicture = new StackPane();

                    // Add both avatars to the StackPane
                    profilePicture.getChildren().addAll(profilePicBackground, profilePicBody, profilePicAcc);

                    HBox playerBox = new HBox();
                    playerBox.getChildren().addAll(profilePicture, usernameText, spacer);
                    playerBox.setAlignment(Pos.CENTER_LEFT);
                    playerBox.setSpacing(10);


                    if (playerInfo.isAdmin()){
                        ImageView adminCrown = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/icons/crownIcon.png"))));
                        adminCrown.setFitHeight(15);
                        adminCrown.setFitWidth(15);
                        HBox.setMargin(adminCrown, new Insets(0, 20, 0, 0));
                        playerBox.getChildren().add(adminCrown);
                    }

                    if (isCurrentUserAdmin) {

                        if (!playerInfo.isAdmin()){
                            ImageView dotsMenu = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/icons/dotsMenuIcon.png"))));
                            dotsMenu.setFitHeight(15);
                            dotsMenu.setFitWidth(4);
                            dotsMenu.setCursor(Cursor.HAND);

                            dotsMenu.setOnMouseClicked(event -> {

                                ContextMenu contextMenu = new ContextMenu();

                                MenuItem kick = new MenuItem("Kick");
                                MenuItem promote = new MenuItem("Promote");

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
                break;
            case "chatMessage":

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
                if(chatMessage.getSender().equals(ClientSession.getUsername())) {
                    sender.setText(LanguageManager.get("text.you") + ": ");
                    message.setTextFill(Color.GRAY);
                    sender.setTextFill(Color.BLACK);
                    //messageBox.setAlignment(Pos.TOP_RIGHT);
                }
                chatVbox.getChildren().add(messageBox);

                Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
                break;

            case "playerKicked":

                Alert alert = new Alert(Alert.AlertType.ERROR, "Connection lost. You have been kicked!", ButtonType.OK);
                alert.showAndWait();


                break;

            case "newAdmin":



        }
    }

    @FXML
    public void initialize() {
        chatVbox.setPadding(new Insets(10, 5, 10, 5));
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setFitToHeight(false);

        chatScrollPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        chatVbox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

        prevMapButton.setOnAction(e -> {});

    }

    @Override
    public void setVirtualServer(VirtualServer virtualServer) throws JsonProcessingException {
        this.virtualServer = virtualServer;

        this.virtualServer.setUpdateUICallBack(new UpdateUICallBack() {
            @Override
            public void process(String answer) throws JsonProcessingException {

                System.out.println("Callback received: " + answer);
                safeUpdateView(answer);
            }

            @Override
            public void disconnect() {

                Platform.runLater(() -> {
                    SceneSwitch.switchScene("/it/polimi/galaxy_trucker/View/hello-view.fxml");
                });
            }
        });
    }

    @FXML
    private void onEnterKeyPressed(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {

            ChatMessage chatMessage = new ChatMessage(chatTextField.getText());
            this.virtualServer.sendCmd(chatMessage);
            chatTextField.setText("");
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
