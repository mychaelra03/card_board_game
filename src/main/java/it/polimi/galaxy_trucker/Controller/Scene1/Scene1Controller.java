package it.polimi.galaxy_trucker.Controller.Scene1;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.galaxy_trucker.Model.PlayerUsername;
import it.polimi.galaxy_trucker.Model.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;

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

        PlayerUsername.getInstance().setUsername(usernameTextField.getText());
        PlayerUsername.getInstance().setPlayerID(1);
        PlayerUsername.getInstance().setColorP(colorMenuButton.getText());
        PlayerUsername.getInstance().setPlayerPic(22);


        ObjectMapper om = new ObjectMapper();

        try{
            File file = new File("src/main/resources/it/polimi/galaxy_trucker/public/player1.json");

            om.writeValue(file,PlayerUsername.getInstance());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        SceneSwitch sceneSwitch = new SceneSwitch();
        sceneSwitch.SceneSwitch(scene1AnchorPane,"view/scene2-view.fxml");
    }

}

