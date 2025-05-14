package it.polimi.galaxy_trucker.client.popups;

public interface PopupAware {
    void bindPopup(Popup popup);
    void setPopupVisible(boolean visible);
    boolean isPopupVisible();
    void initialize(Object... args);
    void onClose();
}

