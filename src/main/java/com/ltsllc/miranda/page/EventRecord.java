package com.ltsllc.miranda.page;

/**
 * Some data that the systen keep on each {@link com.ltsllc.miranda.clientinterface.basicclasses.Event}
 */
public class EventRecord {
    private boolean hasBeenWriten;
    private boolean isOnline;

    public boolean getHasBeenWriten() {
        return hasBeenWriten;
    }

    public void setHasBeenWriten(boolean hasBeenWriten) {
        this.hasBeenWriten = hasBeenWriten;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
