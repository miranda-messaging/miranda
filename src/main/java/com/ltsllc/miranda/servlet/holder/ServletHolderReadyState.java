package com.ltsllc.miranda.servlet.holder;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.session.messages.CheckSessionResponseMessage;

/**
 * Created by Clark on 4/28/2017.
 */
public class ServletHolderReadyState extends State {
    public ServletHolder getServletHolder () {
        return (ServletHolder) getContainer();
    }

    public ServletHolderReadyState (ServletHolder servletHolder) {
        super(servletHolder);
    }

    @Override
    public State processMessage(Message message) {
        State nextState = getServletHolder().getCurrentState();

        switch (message.getSubject()) {
            case CheckSessionResponse: {
                CheckSessionResponseMessage checkSessionResponseMessage = (CheckSessionResponseMessage) message;
                nextState = processCheckSessionResponseMessage (checkSessionResponseMessage);
                break;
            }

            default: {
                nextState = processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processCheckSessionResponseMessage (CheckSessionResponseMessage checkSessionResponseMessage) {
        getServletHolder().setSessionAndAwaken(checkSessionResponseMessage.getSession());

        return getServletHolder().getCurrentState();
    }
}
