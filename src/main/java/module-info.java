module it.polimi.galaxy_trucker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires java.sql;

    opens it.polimi.galaxy_trucker to javafx.fxml;
    exports it.polimi.galaxy_trucker;
    exports it.polimi.galaxy_trucker.server.model.tiles;
    exports it.polimi.galaxy_trucker.server.model.cards;
    opens it.polimi.galaxy_trucker.server.model.cards to com.fasterxml.jackson.databind;
    opens it.polimi.galaxy_trucker.server.model.tiles to com.fasterxml.jackson.databind;
    exports it.polimi.galaxy_trucker.server.model.cards.projectile;
    opens it.polimi.galaxy_trucker.server.model.cards.projectile to com.fasterxml.jackson.databind;
    exports it.polimi.galaxy_trucker.client;
    opens it.polimi.galaxy_trucker.client to javafx.fxml;
    exports it.polimi.galaxy_trucker.client.controllers.shipBuilding;
    opens it.polimi.galaxy_trucker.client.controllers.shipBuilding to javafx.fxml;
    exports it.polimi.galaxy_trucker.common;
    opens it.polimi.galaxy_trucker.common to com.fasterxml.jackson.databind;
    opens it.polimi.galaxy_trucker.client.controllers to javafx.fxml;
    exports it.polimi.galaxy_trucker.client.controllers;
    exports it.polimi.galaxy_trucker.client.popups;
    opens it.polimi.galaxy_trucker.client.popups to javafx.fxml;
    exports it.polimi.galaxy_trucker.common.messages;
    opens it.polimi.galaxy_trucker.common.messages to com.fasterxml.jackson.databind;
    exports it.polimi.galaxy_trucker.common.messages.shipBuilding;
    opens it.polimi.galaxy_trucker.common.messages.shipBuilding to com.fasterxml.jackson.databind;
    exports it.polimi.galaxy_trucker.common.messages.lobby;
    opens it.polimi.galaxy_trucker.common.messages.lobby to com.fasterxml.jackson.databind;
    exports it.polimi.galaxy_trucker.common.messages.login;
    opens it.polimi.galaxy_trucker.common.messages.login to com.fasterxml.jackson.databind;
}