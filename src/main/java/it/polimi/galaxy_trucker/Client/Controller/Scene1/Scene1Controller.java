package it.polimi.galaxy_trucker.Client.Controller.Scene1;
import it.polimi.galaxy_trucker.Client.VirtualServer;
import it.polimi.galaxy_trucker.Server.Model.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class Scene1Controller {

    @FXML
    private AnchorPane scene1AnchorPane;

    @FXML
    private TextField usernameTextField;

    @FXML
    private MenuButton colorMenuButton,pilotMenuButton;


    @FXML
    public void initialize() {
        for(MenuItem mi : colorMenuButton.getItems()) {
            mi.setOnAction(event -> {
                colorMenuButton.setText(mi.getText());
            });
        }
        for(MenuItem mi : pilotMenuButton.getItems()) {
            mi.setOnAction(event -> {
                pilotMenuButton.setText(mi.getText());
            });
        }
    }
    @FXML
    void onClickStart(ActionEvent event) throws IOException {

        String username = usernameTextField.getText();
        this.virtualServer.sendCmd(username);
        String color = colorMenuButton.getText();
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

        SceneSwitch.switchScene(scene1AnchorPane,"/it/polimi/galaxy_trucker/View/scene2-view.fxml");
    }

    private Stage stage;
    VirtualServer virtualServer;
    public void setStage(Stage stage, VirtualServer virtualServer) {
        this.stage = stage;
        this.virtualServer = virtualServer;
    }

}

