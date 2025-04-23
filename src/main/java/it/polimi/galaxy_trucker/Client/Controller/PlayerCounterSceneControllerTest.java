package it.polimi.galaxy_trucker.Client.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Client.ClientSession;
import it.polimi.galaxy_trucker.Client.UpdateUICallBack;
import it.polimi.galaxy_trucker.Client.VirtualServer;
import it.polimi.galaxy_trucker.Client.VirtualServerAware;
import it.polimi.galaxy_trucker.Common.ChatMessage;
import it.polimi.galaxy_trucker.Common.PlayerInfo;
import it.polimi.galaxy_trucker.Common.PlayersInfoMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class PlayerCounterSceneControllerTest implements VirtualServerAware {

    @FXML
    private Label playerCounterLabel;

    @FXML
    private AnchorPane anchorPaneLobby;

    @FXML
    private VBox playerList;

    @FXML
    private TextField chatTextField;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private VBox chatVbox;

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


                if (playersInfo.size() > Integer.parseInt(playerCounterLabel.getText())) {

                    Label playerEnteredLabel = new Label(playersInfoMessage.getUsername() + " joined the lobby");
                    playerEnteredLabel.setStyle("-fx-text-fill: green;");
                    playerEnteredLabel.setPrefWidth(chatScrollPane.getWidth());
                    playerEnteredLabel.setAlignment(Pos.CENTER);

                    chatVbox.getChildren().add(playerEnteredLabel);
                } else {

                    Label playerLeftLabel = new Label(playersInfoMessage.getUsername() + " left the lobby");
                    playerLeftLabel.setStyle("-fx-text-fill: red;");
                    playerLeftLabel.setPrefWidth(chatScrollPane.getWidth());
                    playerLeftLabel.setAlignment(Pos.CENTER);

                    chatVbox.getChildren().add(playerLeftLabel);
                }

                playerCounterLabel.setText(String.valueOf(playersInfo.size()));


                for (PlayerInfo playerInfo : playersInfo) {

                    ImageView avatarPic1 = new ImageView();
                    ImageView avatarPic2 = new ImageView();
                    Image image1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/characters/character_" + playerInfo.getProfilePicIds()[0] + ".png")));
                    Image image2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/textures/characters/hand_" + playerInfo.getProfilePicIds()[1] + ".png")));

                    avatarPic1.setImage(image1);
                    avatarPic1.setFitHeight(PROFILE_PIC_SIZE);
                    avatarPic1.setFitWidth(PROFILE_PIC_SIZE);

                    avatarPic2.setImage(image2);
                    avatarPic2.setFitHeight(PROFILE_PIC_SIZE);
                    avatarPic2.setFitWidth(PROFILE_PIC_SIZE);

                    Label usernameLabel = new Label();
                    usernameLabel.setText(playerInfo.getUsername());
                    if (playerInfo.getUsername().equals(ClientSession.getUsername())){

                       usernameLabel.setStyle("-fx-text-fill: green;");
                    }


                    double radius = PROFILE_PIC_SIZE / 2;
                    Circle backgroundCircle = new Circle(avatarPic1.getX() + PROFILE_PIC_SIZE / 2, avatarPic1.getY() + PROFILE_PIC_SIZE / 2, radius);


                    backgroundCircle.setFill(Paint.valueOf(playerInfo.getColor()));
                    backgroundCircle.setStroke(Color.BLACK);
                    backgroundCircle.setStrokeWidth(1);

                    // Add a circle clip to the first avatar image to make it circular
                    Circle clip = new Circle(radius);
                    clip.setCenterX(avatarPic1.getFitWidth() / 2); // Center the clip within the ImageView
                    clip.setCenterY(avatarPic1.getFitHeight() / 2);
                    avatarPic1.setClip(clip);

                    Circle clip2 = new Circle(radius);
                    clip2.setCenterX(avatarPic2.getFitWidth() / 2); // Center the clip within the ImageView
                    clip2.setCenterY(avatarPic2.getFitHeight() / 2);
                    avatarPic2.setClip(clip2);

                    StackPane stackPane = new StackPane();

                    HBox hBox = new HBox();


                    playerList.setSpacing(10);
                    playerList.setPadding(new Insets(10, 5, 10, 5));

                    playerList.getChildren().add(hBox);

                    hBox.getChildren().addAll(stackPane, usernameLabel);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setSpacing(10);

                    // Add both avatars to the StackPane
                    stackPane.getChildren().addAll(backgroundCircle, avatarPic1, avatarPic2);


                }
                break;
            case "chatMessage":


                ChatMessage chatMessage = mapper.readValue(cmd, ChatMessage.class);



                Label messageLabel = new Label();
                messageLabel.setPrefWidth(chatVbox.getWidth());
                messageLabel.setText(chatMessage.getSender() + ": " + chatMessage.getMessage());

                chatVbox.getChildren().add(messageLabel);
                if (chatMessage.getSender().equals(ClientSession.getUsername())) {
                    messageLabel.setStyle("-fx-text-fill: darkgreen;");
                    messageLabel.setAlignment(Pos.CENTER_RIGHT);
                }

                chatScrollPane.setVvalue(1.0);


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




}
