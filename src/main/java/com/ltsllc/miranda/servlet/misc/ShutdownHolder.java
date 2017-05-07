package com.ltsllc.miranda.servlet.misc;

import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.holder.ServletHolder;

import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 5/1/2017.
 */
public class ShutdownHolder extends ServletHolder {
    private static ShutdownHolder ourInstance;

    public static synchronized void initialize (long timeout) {
        ourInstance = new ShutdownHolder(timeout);
    }

    public static ShutdownHolder getInstance () {
        return ourInstance;
    }

    public ShutdownHolder (long timeout) {
        super("shutdown holder", timeout);
    }

    public void shutdownMirada () throws TimeoutException {
        Miranda.getInstance().sendShutdown(getQueue(), this);

        sleep();
    }
}
