package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.timer.MirandaTimer;
import com.ltsllc.miranda.timer.SchedulePeriodicMessage;
import org.apache.log4j.Logger;

public class MirandaPanicPolicy extends Consumer implements PanicPolicy {
    private static Logger logger = Logger.getLogger(MirandaPanicPolicy.class);

    private int maxPanicCount;
    private int panicCount;
    private Miranda miranda;
    private long timeout;
    private MirandaTimer timer;

    public long getTimeout() {
        return timeout;
    }

    public MirandaTimer getTimer() {
        return timer;
    }

    public boolean beyondMaxCount () {
        return maxPanicCount > panicCount;
    }

    public void decrementPanicCount () {
        panicCount--;

        if (panicCount < 0)
            panicCount = 0;
    }

    public Miranda getMiranda() {
        return miranda;
    }

    public int getPanicCount() {
        return panicCount;
    }

    public void incrementPanicCount () {
        panicCount++;
    }

    public MirandaPanicPolicy (int maxPanicCount, long timeout, Miranda miranda, MirandaTimer timer) {
        super("panic policy");

        this.maxPanicCount = maxPanicCount;
        this.panicCount = 0;
        this.miranda = miranda;
        this.timeout = timeout;
        this.timer = timer;
    }

    private static final long ONE_HOUR = 60 * 60 * 1000;

    public boolean panic (Panic panic) {
        String fatalMessage = "The system is terminating due to a panic";
        boolean keepGoing = false;

        if (panic instanceof StartupPanic) {
            logger.fatal(fatalMessage, panic);
            System.exit(1);
        }

        if (
                panic.getReason() == Panic.Reasons.DoesNotUnderstand ||
                panic.getReason() == Panic.Reasons.ExceptionGettingNextMessage
        )
        {
            incrementPanicCount();
            if (beyondMaxCount()) {
                fatalMessage = "Too many panics: " + getPanicCount();
            }

            DecrementPanicCountMessage decrementMessage = new DecrementPanicCountMessage(getQueue(), this);
            Miranda.timer.schedulePeriodic(ONE_HOUR, getQueue(), decrementMessage);

            keepGoing = true;
        }

        if (!keepGoing) {
            logger.fatal(fatalMessage, panic);
            System.exit(1);
        }

        return keepGoing;
    }
}
