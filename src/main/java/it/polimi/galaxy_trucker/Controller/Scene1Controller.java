package it.polimi.galaxy_trucker.Controller;
import it.polimi.galaxy_trucker.Model.PlayerUsername;
import it.polimi.galaxy_trucker.Model.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Scene1Controller {

    @FXML
    private AnchorPane scene1AnchorPane;

    @FXML
    private TextField usernameTextField;

    @FXML
    void onClickStart(ActionEvent event) throws IOException {

        PlayerUsername.username = usernameTextField.getText();
        SceneSwitch sceneSwitch = new SceneSwitch();
        sceneSwitch.SceneSwitch(scene1AnchorPane,"view/scene2-view.fxml");
    }

}

