module it.polimi.galaxy_trucker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires java.sql;

    opens it.polimi.galaxy_trucker to javafx.fxml;
    exports it.polimi.galaxy_trucker;
    exports it.polimi.galaxy_trucker.Server.Model.tiles;
    exports it.polimi.galaxy_trucker.serverClientTest;
    opens it.polimi.galaxy_trucker.serverClientTest to javafx.fxml;
    exports it.polimi.galaxy_trucker.Server.Model;
    exports it.polimi.galaxy_trucker.Client.Controller.Scene1;
    opens it.polimi.galaxy_trucker.Client.Controller.Scene1 to javafx.fxml;
    exports it.polimi.galaxy_trucker.ServerCliChat;
    exports it.polimi.galaxy_trucker.Server.Model.cards;
    opens it.polimi.galaxy_trucker.ServerCliChat to javafx.fxml;
    opens it.polimi.galaxy_trucker.Server.Model.cards to com.fasterxml.jackson.databind;
    opens it.polimi.galaxy_trucker.Server.Model.tiles to com.fasterxml.jackson.databind;
    exports it.polimi.galaxy_trucker.Server.Model.cards.projectile;
    opens it.polimi.galaxy_trucker.Server.Model.cards.projectile to com.fasterxml.jackson.databind;
    exports it.polimi.galaxy_trucker.Client;
    opens it.polimi.galaxy_trucker.Client to javafx.fxml;
    exports it.polimi.galaxy_trucker.Client.Controller.Scene2;
    opens it.polimi.galaxy_trucker.Client.Controller.Scene2 to javafx.fxml;


}