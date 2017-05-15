package com.ltsllc.miranda.misc;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.ShutdownMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.miranda.messages.StopMessage;

/**
 * Created by Clark on 5/15/2017.
 */
public class ThreadHolderReadyState extends State {
    public ThreadHolder getThreadHolder () {
        return (ThreadHolder) getContainer();
    }

    public ThreadHolderReadyState (ThreadHolder threadHolder) {
        super(threadHolder);
    }

    public State processMessage (Message message) {
        State nextState = getThreadHolder().getCurrentState();

        switch (message.getSubject()) {
            case Shutdown: {
                ShutdownMessage shutdownMessage = (ShutdownMessage) message;
                nextState = processShutdownMessage(shutdownMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processShutdownMessage (ShutdownMessage shutdownMessage) {
        getThreadHolder().shutdown();

        return StopState.getInstance();
    }
}
