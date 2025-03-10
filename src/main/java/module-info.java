module it.polimi.galaxy_trucker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens it.polimi.galaxy_trucker to javafx.fxml;
    exports it.polimi.galaxy_trucker;
    exports it.polimi.galaxy_trucker.Controller;
    opens it.polimi.galaxy_trucker.Controller to javafx.fxml;
}