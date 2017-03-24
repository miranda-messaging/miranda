package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.timer.MirandaTimer;
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

    public void start () {
        DecrementPanicCountMessage decrementMessage = new DecrementPanicCountMessage(getQueue(), this);
        Miranda.timer.sendSchedulePeriodic(ONE_HOUR, getQueue(), decrementMessage);
    }

    public boolean panic (Panic panic) {
        String fatalMessage = "The system is terminating due to a panic";
        boolean continuePanic = false;

        if (panic instanceof StartupPanic) {
            continuePanic = true;
        } else if (
                panic.getReason() == Panic.Reasons.DoesNotUnderstand ||
                panic.getReason() == Panic.Reasons.ExceptionGettingNextMessage ||
                panic.getReason() == Panic.Reasons.ExceptionDuringNetworkSend ||
                panic.getReason() == Panic.Reasons.ExceptionWritingFile ||
                panic.getReason() == Panic.Reasons.CouldNotWrite
        )
        {
            handleCountablePanic(panic);
        } else if (panic.getReason() == Panic.Reasons.DoesNotUnderstandNetworkMessage) {
            handleIgnoreablePanic(panic);
        }

        if (continuePanic) {
            logger.fatal(fatalMessage, panic);
            System.exit(1);
        }

        return continuePanic;
    }

    public void handleCountablePanic (Panic panic) {
        String message = "A panic occurred.";
        logger.error (message, panic.getCause());

        incrementPanicCount();
        if (beyondMaxCount()) {
            String shutDownMessage = getPanicCount() + " panics in " + getTimeout() + "ms.  "
                    + "The system will shut down,";

            logger.error(shutDownMessage);
            getMiranda().shutdown();
        } else {
            String continueMessage = "The system will attempt to continue.";
            logger.error(continueMessage);
        }

        logger.error("A panic happend.  The system will attempt to continue", panic.getCause());
    }

    public void handleIgnoreablePanic (Panic panic) {
        String message = "Received network message the receiver does not know how to process.  "
                + "Will ignore and attempt to continue.";
        logger.error(message, panic.getCause());
    }
}
