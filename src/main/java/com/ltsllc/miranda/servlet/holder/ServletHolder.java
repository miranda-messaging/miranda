package com.ltsllc.miranda.servlet.holder;


import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.states.ServletHolderReadyState;
import org.apache.log4j.Logger;

/**
 * A class that gives servlets the ability to send and receive messages
 */
public class ServletHolder extends Consumer {
    private static Logger logger = Logger.getLogger(ServletHolder.class);

    private boolean awakened;

    public boolean getAwakened() {
        return awakened;
    }

    public void setAwakened(boolean awakened) {
        this.awakened = awakened;
    }

    public ServletHolder (String name) {
        super (name);

        ServletHolderReadyState servletHolderReadyState = new ServletHolderReadyState(this);
        setCurrentState(servletHolderReadyState);
    }

    public ServletHolder (String name, State state) {
        super(name);

        setCurrentState(state);
    }

    /**
     * Wait for a notification or until the supplied timeout has passed or until
     * the Thread is interrupted.  This method returns true if the thread was
     * notified, and false if it was interrupted or the time expired.
     *
     * @param timeout The minimum amount of time to wait.
     * @return See above.
     */
    public synchronized boolean waitFor (long timeout) {
        setAwakened(false);

        try  {
            wait(timeout);
        } catch (InterruptedException e) {
            logger.error ("Interrupted while waiting", e);
            return false;
        }

        return getAwakened();
    }

    public synchronized void wake () {
        setAwakened(true);
        notifyAll();
    }
}
