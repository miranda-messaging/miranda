package com.ltsllc.miranda.miranda.operations.subscriptions;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.subsciptions.messages.DeleteSubscriptionResponseMessage;

/**
 * Created by Clark on 4/22/2017.
 */
public class DeleteSubscriptionOperationReadyState extends State {
    public DeleteSubscriptionOperation getDeleteSubscriptionOperation () {
        return (DeleteSubscriptionOperation) getContainer();
    }

    public DeleteSubscriptionOperationReadyState (DeleteSubscriptionOperation deleteSubscriptionOperation) {
        super(deleteSubscriptionOperation);
    }

    public State processMessage (Message message) {
        State nextState = getDeleteSubscriptionOperation().getCurrentState();

        switch (message.getSubject()) {
            case DeleteSubscriptionResponse: {
                DeleteSubscriptionResponseMessage deleteSubscriptionResponseMessage = (DeleteSubscriptionResponseMessage)
                        message;

                nextState = processDeleteSubscriptionResponseMessage (deleteSubscriptionResponseMessage);
                break;
            }
        }

        return nextState;
    }

    public State processDeleteSubscriptionResponseMessage (DeleteSubscriptionResponseMessage deleteSubscriptionResponseMessage) {
        if (deleteSubscriptionResponseMessage.getResult() == Results.Success) {
            Miranda.getInstance().getCluster().sendDeleteSubscriptionMessage(getDeleteSubscriptionOperation().getQueue(),
                    this, getDeleteSubscriptionOperation().getName());
        }

        DeleteSubscriptionResponseMessage deleteSubscriptionResponseMessage2 = new DeleteSubscriptionResponseMessage(
                getDeleteSubscriptionOperation().getQueue(), this, deleteSubscriptionResponseMessage.getResult());
        send(getDeleteSubscriptionOperation().getRequester(), deleteSubscriptionResponseMessage2);

        return StopState.getInstance();
    }
}
