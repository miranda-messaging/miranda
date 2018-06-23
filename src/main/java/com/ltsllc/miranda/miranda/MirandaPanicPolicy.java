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

import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.shutdown.ShutdownException;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.timer.MirandaTimer;
import org.apache.log4j.Logger;

public class MirandaPanicPolicy extends PanicPolicyClass {
    private static Logger logger = Logger.getLogger(MirandaPanicPolicy.class);

    public MirandaPanicPolicy(int maxPanicCount, long timeout, Miranda miranda, MirandaTimer timer) {
        super("panic policy", maxPanicCount, miranda, timeout, timer);
    }

    public void panic(Panic panic) throws ShutdownException {
        logger.error("A panic occurred", panic);
        String fatalMessage = "The system is terminating due to a panic";
        boolean continuePanic = false;

        if (panic instanceof StartupPanic) {
            continuePanic = true;
        } else if (panic.getReason() == Panic.Reasons.OutOfMemory) {
            continuePanic = true;
        } else if (
                panic.getReason() == Panic.Reasons.DoesNotUnderstand ||
                        panic.getReason() == Panic.Reasons.ExceptionGettingNextMessage ||
                        panic.getReason() == Panic.Reasons.ExceptionDuringNetworkSend ||
                        panic.getReason() == Panic.Reasons.ExceptionTryingToRectify ||
                        panic.getReason() == Panic.Reasons.ExceptionWritingFile ||
                        panic.getReason() == Panic.Reasons.CouldNotWrite
                ) {
            handleCountablePanic(panic);
        } else if (panic.getReason() == Panic.Reasons.DoesNotUnderstandNetworkMessage) {
            handleIgnorablePanic(panic);
        }

        if (continuePanic) {
            logger.error(fatalMessage, panic);
            System.exit(-1);
        }
    }

}
