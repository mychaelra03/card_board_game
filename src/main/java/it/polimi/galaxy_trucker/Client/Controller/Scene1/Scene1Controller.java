package it.polimi.galaxy_trucker.Client.Controller.Scene1;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Client.VirtualServer;
import it.polimi.galaxy_trucker.GameLogic;
import it.polimi.galaxy_trucker.Model.PlayerUsername;
import it.polimi.galaxy_trucker.Model.SceneSwitch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Scene1Controller {

    @FXML
    private AnchorPane scene1AnchorPane;

    @FXML
    private TextField usernameTextField;

   // @FXML
    //private MenuButton colorMenuButton,pilotMenuButton;

    @FXML
    private ImageView myImageView;


    @FXML
    public void initialize() {

        Platform.runLater(() -> {
            myImageView.fitWidthProperty().bind(myImageView.getScene().widthProperty().subtract(205));
            myImageView.fitHeightProperty().bind(myImageView.getScene().heightProperty().subtract(290));
            AnchorPane parent = (AnchorPane) myImageView.getParent();
            myImageView.layoutXProperty().bind(
                    parent.widthProperty().subtract(myImageView.fitWidthProperty()).divide(2)
            );
        });
        /*for(MenuItem mi : colorMenuButton.getItems()) {
            mi.setOnAction(event -> {
                colorMenuButton.setText(mi.getText());
            });
        }
        for(MenuItem mi : pilotMenuButton.getItems()) {
            mi.setOnAction(event -> {
                pilotMenuButton.setText(mi.getText());
            });
        }*/
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


        SceneSwitch sceneSwitch = new SceneSwitch();
        sceneSwitch.switchScene(scene1AnchorPane,"/it/polimi/galaxy_trucker/View/login-view.fxml");
    }

    private Stage stage;
    VirtualServer virtualServer;
    public void setStage(Stage stage, VirtualServer virtualServer) {
        this.stage = stage;
        this.virtualServer = virtualServer;
    }

}

