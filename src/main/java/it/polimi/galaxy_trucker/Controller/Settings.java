package it.polimi.galaxy_trucker.Controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Settings {
    private BoxBlur blurEffect = new BoxBlur(5, 5, 5);
    private AnchorPane scene2AnchorPane;

    public Settings(AnchorPane scene2AnchorPane) {
        this.scene2AnchorPane = scene2AnchorPane;
    }



    public void showPopupSettings(AnchorPane anchorPane) throws IOException {

        anchorPane.setEffect(blurEffect);

        Stage popupStage = new Stage();
        popupStage.initOwner(scene2AnchorPane.getScene().getWindow());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.TRANSPARENT);

        StackPane popupRoot= new StackPane();
        popupRoot.setStyle("-fx-background-color: rgba(255,255,255,1); -fx-background-radius: 10;");

        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: red; -fx-font-weight: bold; ");
        closeButton.setTextFill(Color.WHITE);


        HBox closeContainer = new HBox(closeButton);
        closeContainer.setAlignment(Pos.TOP_RIGHT);
        closeContainer.setPadding(new Insets(10));

        popupRoot.getChildren().add(closeContainer);

        closeButton.setOnAction(e -> {
            popupStage.close();
            anchorPane.setEffect(null);

        });

        Scene popupScene = new Scene(popupRoot, 300, 500);
        popupScene.setFill(Color.TRANSPARENT); // Scene is also transparent

        popupStage.setScene(popupScene);
        popupStage.show();


    }
}
