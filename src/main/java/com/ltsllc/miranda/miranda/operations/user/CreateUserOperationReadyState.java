package com.ltsllc.miranda.miranda.operations.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.states.OperationState;
import com.ltsllc.miranda.user.messages.CreateUserResponseMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class CreateUserOperationReadyState extends State {
    public CreateUserOperation getCreateUserOperation () {
        return (CreateUserOperation) getContainer();
    }

    public CreateUserOperationReadyState (CreateUserOperation createUserOperation) {
        super(createUserOperation);
    }

    public State processMessage (Message message) {
        State nextState = getCreateUserOperation().getCurrentState();

        switch (message.getSubject()) {
            case CreateUserResponse: {
                CreateUserResponseMessage createUserResponseMessage = (CreateUserResponseMessage) message;
                nextState = processCreateUserResponseMessage(createUserResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processCreateUserResponseMessage (CreateUserResponseMessage message) {
        if (message.getResult() == Results.Success) {
            Miranda.getInstance().getCluster().sendNewUserMessage(getCreateUserOperation().getQueue(),
                    this, message.getUser());
        }

        CreateUserResponseMessage createUserResponseMessage = new CreateUserResponseMessage(
                getCreateUserOperation().getQueue(), this, message.getUser(), message.getResult());

        send(getCreateUserOperation().getRequester(), createUserResponseMessage);

        return StopState.getInstance();
    }
}
