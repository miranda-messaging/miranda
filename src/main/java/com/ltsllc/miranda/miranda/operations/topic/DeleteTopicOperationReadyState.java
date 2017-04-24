package com.ltsllc.miranda.miranda.operations.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.topics.messages.DeleteTopicResponseMessage;

/**
 * Created by Clark on 4/23/2017.
 */
public class DeleteTopicOperationReadyState extends State {
    public DeleteTopicOperation getDeleteTopicOperation () {
        return (DeleteTopicOperation) getContainer();
    }

    public DeleteTopicOperationReadyState (DeleteTopicOperation deleteTopicOperation) {
        super(deleteTopicOperation);
    }

    public State processMessage (Message message) {
        State nextState = getDeleteTopicOperation().getCurrentState();

        switch (message.getSubject()) {
            case DeleteTopicResponse: {
                DeleteTopicResponseMessage deleteTopicResponseMessage = (DeleteTopicResponseMessage) message;
                nextState = processDeleteTopicResponseMessage(deleteTopicResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processDeleteTopicResponseMessage(DeleteTopicResponseMessage deleteTopicResponseMessage) {
        DeleteTopicResponseMessage response = new DeleteTopicResponseMessage(getDeleteTopicOperation().getQueue(),
                this, deleteTopicResponseMessage.getResult());

        send(getDeleteTopicOperation().getRequester(), response);

        return StopState.getInstance();
    }
}
