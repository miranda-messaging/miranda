package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.miranda.messages.DecrementPanicCountMessage;
import com.ltsllc.miranda.timer.MirandaTimer;
import org.apache.log4j.Logger;

public class MirandaPanicPolicy extends PanicPolicyClass {
    private static Logger logger = Logger.getLogger(MirandaPanicPolicy.class);

    public MirandaPanicPolicy (int maxPanicCount, long timeout, Miranda miranda, MirandaTimer timer) {
        super("panic policy", maxPanicCount, miranda, timeout, timer);
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
                panic.getReason() == Panic.Reasons.ExceptionTryingToRectify ||
                panic.getReason() == Panic.Reasons.ExceptionWritingFile ||
                panic.getReason() == Panic.Reasons.CouldNotWrite
        )
        {
            handleCountablePanic(panic);
        } else if (panic.getReason() == Panic.Reasons.DoesNotUnderstandNetworkMessage) {
            handleIgnorablePanic(panic);
        }

        if (continuePanic) {
            logger.fatal(fatalMessage, panic);
            if (!getTestMode())
                System.exit(1);
        }

        return continuePanic;
    }

}
