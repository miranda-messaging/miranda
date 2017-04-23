package com.ltsllc.miranda.session.operations;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.states.OperationState;
import com.ltsllc.miranda.session.messages.CreateSessionResponseMessage;
import com.ltsllc.miranda.user.messages.GetUserResponseMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/18/2017.
 */
public class CreateSessionOperationReadyState extends State {
    public CreateSessionOperation getCreateSessionOperation () {
        return (CreateSessionOperation) getContainer();
    }

    public CreateSessionOperationReadyState (CreateSessionOperation createSessionOperation) {
        super(createSessionOperation);
    }

    public State processMessage (Message message) {
        State nextState = getCreateSessionOperation().getCurrentState();

        switch (message.getSubject()) {
            case GetUserResponse: {
                GetUserResponseMessage getUserResponseMessage = (GetUserResponseMessage) message;
                nextState = processGetUserResponseMessage(getUserResponseMessage);
                break;
            }

            case CreateSessionResponse: {
                CreateSessionResponseMessage createSessionResponseMessage = (CreateSessionResponseMessage) message;
                nextState = processCreateSessionResponseMessage(createSessionResponseMessage);
                break;
            }
        }
        return nextState;
    }

    public State processGetUserResponseMessage (GetUserResponseMessage getUserResponseMessage) {
        if (getUserResponseMessage.getResult() == Results.Success) {
            Miranda.getInstance().getSessionManager().sendCreateSession(getCreateSessionOperation().getQueue(),
                    this, getUserResponseMessage.getUser());

            return getCreateSessionOperation().getCurrentState();
        } else {
            CreateSessionResponseMessage response = new CreateSessionResponseMessage(getCreateSessionOperation().getQueue(),
                    this, Results.UserNotFound, null);
            send (getCreateSessionOperation().getRequester(), response);

            return StopState.getInstance();
        }
    }

    public State processCreateSessionResponseMessage (CreateSessionResponseMessage createSessionResponseMessage) {
        CreateSessionResponseMessage response = new CreateSessionResponseMessage(getCreateSessionOperation().getQueue(),
                this, createSessionResponseMessage.getResult(), createSessionResponseMessage.getSession());

        send (getCreateSessionOperation().getRequester(), response);

        return StopState.getInstance();
    }
}
