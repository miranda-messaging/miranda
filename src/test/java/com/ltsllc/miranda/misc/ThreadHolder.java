package com.ltsllc.miranda.misc;

import com.ltsllc.miranda.Consumer;

/**
 * Created by Clark on 5/15/2017.
 */
abstract public class ThreadHolder extends Consumer {
    abstract MirandaThread createThread ();

    private MirandaThread mirandaThread;

    public ThreadHolder (String name) {
        super(name);
    }

    public MirandaThread getMirandaThread() {
        return mirandaThread;
    }

    public void setMirandaThread(MirandaThread mirandaThread) {
        this.mirandaThread = mirandaThread;

        ThreadHolderReadyState threadHolderReadyState = new ThreadHolderReadyState(this);
        setCurrentState(threadHolderReadyState);
    }

    public void start () {
        mirandaThread = createThread();
        mirandaThread.start();
    }

    public void stop () {
        getMirandaThread().stop();

        super.stop();
    }
}
