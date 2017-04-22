package com.ltsllc.miranda.miranda.operations.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.states.OperationState;
import com.ltsllc.miranda.user.messages.UpdateUserResponseMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class UpdateUserOperationReadyState extends OperationState {
    public UpdateUserOperation getUpdateUserOperation () {
        return (UpdateUserOperation) getContainer();
    }

    public UpdateUserOperationReadyState (UpdateUserOperation updateUserOperation, BlockingQueue<Message> requester) {
        super(updateUserOperation, requester);
    }

    public State processMessage (Message message) {
        State nextState = getUpdateUserOperation().getCurrentState();

        switch (message.getSubject()) {
            case UpdateUserResponse: {
                UpdateUserResponseMessage updateUserResponseMessage = (UpdateUserResponseMessage) message;
                nextState = processUpdateUserResponseMessage (updateUserResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processUpdateUserResponseMessage (UpdateUserResponseMessage message) {
        if (message.getResult() == Results.Success) {
            Miranda.getInstance().getCluster().sendUpdateUserMessage(getUpdateUserOperation().getQueue(), this,
                    message.getUser());
        }

        UpdateUserResponseMessage updateUserResponseMessage = new UpdateUserResponseMessage(
                getUpdateUserOperation().getQueue(), this, message.getUser(), message.getResult());

        send (getRequester(), updateUserResponseMessage);

        return StopState.getInstance();
    }
}
