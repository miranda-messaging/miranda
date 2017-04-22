package com.ltsllc.miranda.servlet.login;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.session.LoginResponseMessage;
import com.ltsllc.miranda.session.messages.GetSessionResponseMessage;

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
            case LoginResponse: {
                LoginResponseMessage loginResponseMessage = (LoginResponseMessage) message;
                nextState = processLoginResponseMessage(loginResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processGetSessionResponseMessage (GetSessionResponseMessage getSessionResponseMessage) {
        if (getSessionResponseMessage.getResult() == Results.Success)
                getLoginHolder().setSession(getSessionResponseMessage.getSession());

        LoginHolder.LoginResult loginResult = new LoginHolder.LoginResult(getSessionResponseMessage.getResult(),
                getSessionResponseMessage.getSession());

        getLoginHolder().setResultAndWakeup(loginResult);

        return getLoginHolder().getCurrentState();
    }

    public State processLoginResponseMessage (LoginResponseMessage loginResponseMessage) {
        LoginHolder.LoginResult loginResult = new LoginHolder.LoginResult(loginResponseMessage.getResult(),
                loginResponseMessage.getSession());

        getLoginHolder().setResultAndWakeup(loginResult);

        return getLoginHolder().getCurrentState();
    }
}
