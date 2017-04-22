package com.ltsllc.miranda.miranda.operations.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.states.OperationState;
import com.ltsllc.miranda.topics.messages.CreateTopicResponseMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class CreateTopicOperationReadyState extends OperationState {
    public CreateTopicOperation getCreateTopicOperation () {
        return (CreateTopicOperation) getContainer();
    }

    public CreateTopicOperationReadyState (CreateTopicOperation createTopicOperation, BlockingQueue<Message> requester) {
        super(createTopicOperation, requester);
    }

    public State processMessage (Message message) {
        State nextState = getCreateTopicOperation().getCurrentState();

        switch (message.getSubject()) {
            case CreateTopicResponse: {
                CreateTopicResponseMessage createTopicResponseMessage = (CreateTopicResponseMessage) message;
                nextState = processCreateTopicResponseMessage (createTopicResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processCreateTopicResponseMessage (CreateTopicResponseMessage message) {
        if (message.getResult() == Results.Success) {
            Miranda.getInstance().getCluster().sendNewTopicMessage (getCreateTopicOperation().getQueue(), this,
                    getCreateTopicOperation().getTopic());
        }

        CreateTopicResponseMessage createTopicResponseMessage = new CreateTopicResponseMessage(
                getCreateTopicOperation().getQueue(), this, message.getResult());

        send (getRequester(), createTopicResponseMessage);

        return StopState.getInstance();
    }
}
