package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;

/**
 * Created by Clark on 3/8/2017.
 */
public class PanicPolicyReadyState extends State {
    public PanicPolicyReadyState (MirandaPanicPolicy panicPolicy) {
        super(panicPolicy);
    }

    public MirandaPanicPolicy getMirandaPanicPolicy () {
        return (MirandaPanicPolicy) getContainer();
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()){
            case DecrementPanicCount: {
                DecrementPanicCountMessage decrementPanicCountMessage = (DecrementPanicCountMessage) message;
                nextState = processDecrementPanicCountMessage(decrementPanicCountMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    private State processDecrementPanicCountMessage(DecrementPanicCountMessage message) {
        getMirandaPanicPolicy().decrementPanicCount();

        return this;
    }
}
