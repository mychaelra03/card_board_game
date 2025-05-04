package it.polimi.galaxy_trucker.client.popups;

public interface PopupAware {
    void setPopup(Popup popup);
    void initializeWithArguments(Object... args);
}

