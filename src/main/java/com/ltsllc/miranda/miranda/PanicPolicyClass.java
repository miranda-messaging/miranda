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

package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.messages.DecrementPanicCountMessage;
import com.ltsllc.miranda.timer.MirandaTimer;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 5/15/2017.
 */
abstract public class PanicPolicyClass extends Consumer implements PanicPolicy {
    private static Logger logger = Logger.getLogger(PanicPolicyClass.class);

    private MirandaTimer timer;
    private boolean testMode;
    private int maxPanicCount;
    private int panicCount;
    private Miranda miranda;
    private long timeout;

    public PanicPolicyClass(String name, int maxPanicCount, Miranda miranda, long timeout, MirandaTimer timer) {
        super(name);
        this.testMode = false;
        this.maxPanicCount = maxPanicCount;
        this.miranda = miranda;
        this.timeout = timeout;
        this.panicCount = 0;
        this.timer = timer;
    }

    public boolean getTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public long getTimeout() {
        return timeout;
    }

    public Miranda getMiranda() {
        return miranda;
    }

    public int getPanicCount() {
        return panicCount;
    }

    public void setTimer(MirandaTimer timer) {
        this.timer = timer;
    }

    public void setMaxPanicCount(int maxPanicCount) {
        this.maxPanicCount = maxPanicCount;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        PanicPolicyClass.logger = logger;
    }

    public void incrementPanicCount () {
        panicCount++;
    }

    public boolean beyondOrAtMaxCount () {
        return panicCount >= maxPanicCount;
    }

    public void handleCountablePanic (Panic panic) {
        String message = "A panic occurred.";
        logger.error (message, panic.getCause());

        incrementPanicCount();

        if (beyondOrAtMaxCount()) {
            String shutDownMessage = getPanicCount() + " panics in " + getTimeout() + "ms.  "
                    + "The system will shut down,";

            logger.error(shutDownMessage);
            getMiranda().shutdown();
        } else {
            String continueMessage = "The system will attempt to continue.";
            logger.error(continueMessage);
        }
    }

    public void handleIgnorablePanic (Panic panic) {
        String message = "Received network message the receiver does not know how to process.  "
                + "Will ignore and attempt to continue.";
        logger.error(message, panic.getCause());
    }

    public void decrementPanicCount () {
        panicCount--;

        if (panicCount < 0)
            panicCount = 0;
    }

    public void setPanicCount(int panicCount) {
        this.panicCount = panicCount;
    }

    public MirandaTimer getTimer() {
        return timer;
    }

    public void start () {
        DecrementPanicCountMessage decrementPanicCountMessage = new DecrementPanicCountMessage(getQueue(), this);
        getTimer().sendSchedulePeriodic(getTimeout(), getQueue(), decrementPanicCountMessage);
    }
}
