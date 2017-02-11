package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.SynchronizeMessage;

/**
 * Created by Clark on 2/10/2017.
 */
abstract public class SingleFileReadyState<E> extends State {
    abstract public State processSynchronizeMessage(SynchronizeMessage synchronizeMessage);

    public SingleFileReadyState (Consumer consumer) {
        super(consumer);
    }


    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case Synchronize: {
                SynchronizeMessage synchronizeMessage = (SynchronizeMessage) message;
                nextState = processSynchronizeMessage (synchronizeMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }
}
