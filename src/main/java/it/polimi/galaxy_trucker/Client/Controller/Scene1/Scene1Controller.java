package it.polimi.galaxy_trucker.Client.Controller.Scene1;
import it.polimi.galaxy_trucker.Client.VirtualServer;
import it.polimi.galaxy_trucker.Client.VirtualServerAware;
import it.polimi.galaxy_trucker.Server.Model.SceneSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Scene1Controller implements VirtualServerAware {

    @FXML
    private AnchorPane scene1AnchorPane;

    @FXML
    private TextField usernameTextField;

    @FXML
    private MenuButton colorMenuButton,pilotMenuButton;


    @FXML
    public void initialize() {

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

        SceneSwitch sceneSwitch = new SceneSwitch();
        sceneSwitch.switchScene("/it/polimi/galaxy_trucker/View/scene2-view.fxml");
    }

    private Stage stage;
    private VirtualServer virtualServer;

    @Override
    public void setVirtualServer(VirtualServer virtualServer) {
        this.virtualServer = virtualServer;
    }

}

