package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.miranda.Miranda;

/**
 * Created by Clark on 3/9/2017.
 */
public class MirandaStatus extends Consumer {
    private static MirandaStatus ourInstance;

    private StatusObject statusObject;

    public StatusObject getStatusObject() {
        return statusObject;
    }

    public void setStatusObject(StatusObject statusObject) {
        this.statusObject = statusObject;
    }

    public static synchronized void initialize () {
        if (null == ourInstance) {
            ourInstance = new MirandaStatus();
        }
    }

    public static MirandaStatus getInstance () {
        return ourInstance;
    }

    private MirandaStatus () {
        super("miranda status");

        MirandaStatusReadyState mirandaStatusReadyState = new MirandaStatusReadyState(this);
        setCurrentState(mirandaStatusReadyState);
    }

    public void receivedStatus (StatusObject statusObject) {
        setStatusObject(statusObject);

        synchronized (this) {
            notifyAll();
        }
    }

    public StatusObject getStatus () {
        StatusObject statusObject = null;

        try {
            //
            // so that we get a fresh status
            //
            setStatusObject(null);
            Miranda.getInstance().getStatus(getQueue());

            synchronized (this) {
                wait(1000);
            }

            statusObject = getStatusObject();
        } catch (InterruptedException e) {
            //
            // Ignore if we are interrupted
            //
        }

        return statusObject;
    }
}
