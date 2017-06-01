package com.ltsllc.miranda.mina;

/**
 * Created by Clark on 5/31/2017.
 */
public abstract class Runner implements Runnable {
    abstract public void basicRun () throws Exception;

    private Thread thread;
    private Throwable throwable;

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Thread getThread() {
        return thread;
    }

    public void start () {
        getThread().start();
    }

    public Runner () {
        this.thread = new Thread(this);
    }

    public void run () {
        try {
            basicRun();
        } catch (Exception e) {
            setThrowable(e);
        }
    }
}
