package com.ltsllc.miranda.servlet.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.holder.LoginHolder;
import com.ltsllc.miranda.session.CreateSessionMessage;
import com.ltsllc.miranda.session.CreateSessionResponseMessage;
import com.ltsllc.miranda.session.LoginResponseMessage;
import com.ltsllc.miranda.user.messages.GetUserResponseMessage;

/**
 * Created by Clark on 4/1/2017.
 */
public class LoginHolderReadyState extends State {
    public LoginHolder getLoginHolder () {
        return (LoginHolder) getContainer();
    }

    public LoginHolderReadyState (LoginHolder loginHolder) {
        super(loginHolder);
    }

    public State processMessage (Message message) {
        State nextState = getLoginHolder().getCurrentState();

        switch (message.getSubject()) {
            case CreateSessionResponse: {
                CreateSessionResponseMessage createSessionResponseMessage = (CreateSessionResponseMessage) message;
                nextState = processCreateSessionResponseMessage(createSessionResponseMessage);
                break;
            }

            case GetUserResponse: {
                GetUserResponseMessage getUserResponseMessage = (GetUserResponseMessage) message;
                nextState = processGetUserResponseMessage (getUserResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processCreateSessionResponseMessage (CreateSessionResponseMessage createSessionResponseMessage) {
        getLoginHolder().setSessionAndWakeup(createSessionResponseMessage.getSession());

        return getLoginHolder().getCurrentState();
    }

    public State processGetUserResponseMessage (GetUserResponseMessage getUserResponseMessage) {
        getLoginHolder().setUser(getUserResponseMessage.getUser());

        return getLoginHolder().getCurrentState();
    }
}
