package com.ltsllc.miranda.miranda.operations.login;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.states.OperationState;
import com.ltsllc.miranda.session.messages.CreateSessionResponseMessage;
import com.ltsllc.miranda.session.messages.GetSessionResponseMessage;
import com.ltsllc.miranda.session.LoginResponseMessage;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class LoginOperationReadyState extends OperationState {
    private Session session;

    public Session getSession() {
        return session;
    }

    public LoginOperation getLoginOperation () {
        return (LoginOperation) getContainer();
    }

    public LoginOperationReadyState (LoginOperation loginOperation, BlockingQueue<Message> requester) {
        super(loginOperation, requester);
    }

    public State processMessage (Message message) {
        State nextState = getLoginOperation().getCurrentState();

        switch (message.getSubject()) {
            case GetSessionResponse: {
                GetSessionResponseMessage getSessionResponseMessage = (GetSessionResponseMessage) message;
                nextState = processGetSessionResponseMessage(getSessionResponseMessage);
                break;
            }

            case CreateSessionResponse: {
                CreateSessionResponseMessage createSessionResponseMessage = (CreateSessionResponseMessage) message;
                nextState = processCreateSessionResponseMessage(createSessionResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
            }
        }

        return nextState;
    }

    public State processGetSessionResponseMessage (GetSessionResponseMessage getSessionResponseMessage) {
        if (getSessionResponseMessage.getResult() == Results.SessionCreated) {
            Miranda.getInstance().getCluster().sendNewSession(getLoginOperation().getQueue(), this,
                    getSessionResponseMessage.getSession());
        }

        LoginResponseMessage loginResponseMessage = new LoginResponseMessage(getLoginOperation().getQueue(),
                this, getSessionResponseMessage.getResult(), getSessionResponseMessage.getSession());

        send(getRequester(), loginResponseMessage);

        return StopState.getInstance();
    }

    public State processCreateSessionResponseMessage (CreateSessionResponseMessage createSessionResponseMessage) {
        Results loginResult;

        if (createSessionResponseMessage.getResult() == Results.Success)
            loginResult = Results.SessionCreated;
        else
            loginResult = createSessionResponseMessage.getResult();

        LoginResponseMessage resultMessage = new LoginResponseMessage(getLoginOperation().getQueue(),
                this, loginResult, createSessionResponseMessage.getSession());

        send(getRequester(), resultMessage);

        return StopState.getInstance();
    }

}
