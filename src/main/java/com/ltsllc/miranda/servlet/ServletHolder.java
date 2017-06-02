/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.servlet;


import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.session.Session;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeoutException;

/**
 * A class that gives servlets the ability to receive messages
 */
public class ServletHolder extends Consumer {
    private static Logger logger = Logger.getLogger(ServletHolder.class);

    private long timeoutPeriod;
    private boolean awakened;
    private Session session;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public boolean getAwakened() {
        return awakened;
    }

    public void setAwakened(boolean awakened) {
        this.awakened = awakened;
    }

    public long getTimeoutPeriod() {
        return timeoutPeriod;
    }

    public void setTimeoutPeriod(long timeoutPeriod) {
        this.timeoutPeriod = timeoutPeriod;
    }

    public ServletHolder (String name, long timeoutPeriod) {
        super (name);

        this.timeoutPeriod = timeoutPeriod;

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
    public synchronized void waitFor (long timeout) throws TimeoutException {
        setAwakened(false);

        try  {
            wait(timeout);
        } catch (InterruptedException e) {
            logger.error ("Interrupted while waiting", e);
        }

        if (!getAwakened()) {
            throw new TimeoutException("A timeout occurred");
        }
    }

    public void waitFor () throws TimeoutException {
        waitFor(getTimeoutPeriod());
    }

    public void sleep () throws TimeoutException {
        waitFor(getTimeoutPeriod());
    }

    public synchronized void wake () {
        setAwakened(true);
        notifyAll();
    }

    public Session getSession (long sessionId) throws TimeoutException {
        setSession(null);

        Miranda.getInstance().getSessionManager().sendCheckSessionMessage (getQueue(), this, sessionId);

        sleep();

        return getSession();
    }

    public void setSessionAndAwaken (Session session) {
        setSession(session);
        wake();
    }
}
