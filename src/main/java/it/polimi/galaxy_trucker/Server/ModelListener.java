package it.polimi.galaxy_trucker.Server;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ModelListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String debugStr= evt.getPropertyName() + ": " + evt.getOldValue() + " -> " + evt.getNewValue();
        this.callBack.gotEvent(evt);
    }

    private CallBack callBack;
    public ModelListener(CallBack callBack) {
        this.callBack = callBack;
    }
}
