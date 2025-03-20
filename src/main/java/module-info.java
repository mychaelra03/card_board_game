module it.polimi.galaxy_trucker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens it.polimi.galaxy_trucker to javafx.fxml;
    exports it.polimi.galaxy_trucker;
    exports it.polimi.galaxy_trucker.tiles;
    exports it.polimi.galaxy_trucker.serverClientTest;
    opens it.polimi.galaxy_trucker.serverClientTest to javafx.fxml;
}