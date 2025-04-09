package it.polimi.galaxy_trucker.Client.Controller.Scene1;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Client.VirtualServer;
import it.polimi.galaxy_trucker.Client.VirtualServerAware;
import it.polimi.galaxy_trucker.Server.Model.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
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

    private int bodyIndex = 0;
    private int accessoryIndex = 0;
    private static final int MAX_BODIES = 9;
    private static final int MAX_ACCESSORIES = 8;


    @FXML
    public void initialize() {
        BackgroundImage bg = new BackgroundImage(

                new Image(getClass().getResource("/textures/background.png").toExternalForm()),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, true, false)
        );

        scene1AnchorPane.setBackground(new Background(bg));
        System.out.println(getClass().getResource("/textures/background.png"));
        /*Platform.runLater(() -> {
            myImageView.fitWidthProperty().bind(myImageView.getScene().widthProperty().subtract(205));
            myImageView.fitHeightProperty().bind(myImageView.getScene().heightProperty().subtract(290));
            VBox parent = (VBox) myImageView.getParent();
           /* myImageView.layoutXProperty().bind(
                    parent.widthProperty().subtract(myImageView.fitWidthProperty()).divide(2)
            );*/
        //});
        //gameTitle.setImage(loadImage("/textures/title.png"));
        //backgroundView.setImage(loadImage("/textures/characters/bg_blue.png"));
        bodyView.setImage(loadBodyImage(bodyIndex));
        accessoryView.setImage(loadAccessoryImage(accessoryIndex));

        //backgroundImage.toBack();
        String imageUrl = getClass().getResource("/textures/background.png").toExternalForm();
        scene1AnchorPane.setStyle("-fx-background-image: url('" + imageUrl + "');");
        leftBodyBtn.setOnAction(e -> updateBody(-1));
        rightBodyBtn.setOnAction(e -> updateBody(1));
        leftAccessoryBtn.setOnAction(e -> updateAccessory(-1));
        rightAccessoryBtn.setOnAction(e -> updateAccessory(1));
        playButton.setOnAction(e -> validateName());
        // Imposta le dimensioni esplicitamente nel codice (puoi usare il valore che desideri)
        //scene1AnchorPane.setPrefWidth(100);
        //scene1AnchorPane.setPrefHeight(100);
    }
    @FXML
    void onClickStart(ActionEvent event) throws IOException {

        String username = usernameTextField.getText();
        this.virtualServer.sendCmd(username);
        String color = "Rosso";
        this.virtualServer.sendCmd(color.toUpperCase());


//        PlayerUsername.getInstance().setUsername(usernameTextField.getText());
//        PlayerUsername.getInstance().setPlayerID(1);
//        PlayerUsername.getInstance().setColorP(colorMenuButton.getText());
//        PlayerUsername.getInstance().setPlayerPic(22);
//
//
//        ObjectMapper om = new ObjectMapper();
//
//        try{
//            File file = new File("src/main/resources/it/polimi/galaxy_trucker/public/player1.json");
//
//            om.writeValue(file,PlayerUsername.getInstance());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        String fxmlFile = "/it/polimi/galaxy_trucker/View/login-view.fxml";
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));

        // Crea una nuova scena con il root caricato
        Scene scene = new Scene(root);


        SceneSwitch sceneSwitch = new SceneSwitch();
        sceneSwitch.switchScene("/it/polimi/galaxy_trucker/View/scene2-view.fxml");
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

