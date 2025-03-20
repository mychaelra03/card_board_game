module it.polimi.galaxy_trucker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens it.polimi.galaxy_trucker to javafx.fxml;
    exports it.polimi.galaxy_trucker;
    exports it.polimi.galaxy_trucker.Model;
    exports it.polimi.galaxy_trucker.Controller;
    opens it.polimi.galaxy_trucker.Controller to javafx.fxml;
    exports it.polimi.galaxy_trucker.Controller.Scene1;
    opens it.polimi.galaxy_trucker.Controller.Scene1 to javafx.fxml;
    exports it.polimi.galaxy_trucker.ServerCliChat;
    opens it.polimi.galaxy_trucker.ServerCliChat to javafx.fxml;


}