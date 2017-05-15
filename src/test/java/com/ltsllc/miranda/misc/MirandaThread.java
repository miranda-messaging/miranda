package com.ltsllc.miranda.misc;

/**
 * Created by Clark on 5/15/2017.
 */
abstract public class MirandaThread implements Runnable {
    abstract public void performIteration ();

    private boolean keepGoing;
    private Thread thread;
    private ThreadHolder threadHolder;

    public ThreadHolder getThreadHolder() {
        return threadHolder;
    }

    public void setThreadHolder(ThreadHolder threadHolder) {
        this.threadHolder = threadHolder;
    }

    public MirandaThread () {
        keepGoing = true;
    }

    public boolean getKeepGoing() {
        return keepGoing;
    }

    public void setKeepGoing(boolean keepGoing) {
        this.keepGoing = keepGoing;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public synchronized void start () {
        if (null == thread) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public synchronized void stop () {
        if (null != thread) {
            setKeepGoing(false);
            thread.interrupt();
        }
    }

    public void run () {
        while (getKeepGoing()) {
            performIteration();
        }
    }
}
