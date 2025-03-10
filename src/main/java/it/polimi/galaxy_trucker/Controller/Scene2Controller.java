package it.polimi.galaxy_trucker.Controller;

import it.polimi.galaxy_trucker.Model.PlayerUsername;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class Scene2Controller {

    @FXML
    private AnchorPane scene2AnchorPane;

    @FXML
    private Label labelUsername;

    public void initialize() {
        labelUsername.setText("Hi " + PlayerUsername.username);
    }

}

