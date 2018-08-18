package com.ltsllc.miranda.page;

/**
 * Some data that the system keeps on each {@link com.ltsllc.miranda.clientinterface.basicclasses.Event}
 */
public class EventRecord {
    private boolean hasBeenWritten;
    private boolean isOnline;

    public boolean getHasBeenWritten() {
        return hasBeenWritten;
    }

    public void setHasBeenWritten(boolean hasBeenWritten) {
        this.hasBeenWritten = hasBeenWritten;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
