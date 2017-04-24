package com.ltsllc.miranda.miranda.operations.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.operations.states.OperationState;
import com.ltsllc.miranda.topics.messages.CreateTopicResponseMessage;
import com.ltsllc.miranda.user.messages.GetUserResponseMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class CreateTopicOperationReadyState extends State {
    public CreateTopicOperation getCreateTopicOperation () {
        return (CreateTopicOperation) getContainer();
    }

    public CreateTopicOperationReadyState (CreateTopicOperation createTopicOperation) {
        super(createTopicOperation);
    }

    public State processMessage (Message message) {
        State nextState = getCreateTopicOperation().getCurrentState();

        switch (message.getSubject()) {
            case GetUserResponse: {
                GetUserResponseMessage getUserResponseMessage = (GetUserResponseMessage) message;
                nextState = processGetUserResponseMessage (getUserResponseMessage);
                break;
            }

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
        CreateTopicResponseMessage createTopicResponseMessage = new CreateTopicResponseMessage(
                getCreateTopicOperation().getQueue(), this, message.getResult());

        send (getCreateTopicOperation().getRequester(), createTopicResponseMessage);

        return StopState.getInstance();
    }

    public State processGetUserResponseMessage (GetUserResponseMessage getUserResponseMessage) {
        if (getUserResponseMessage.getResult() != Results.Success) {
            CreateTopicResponseMessage createTopicResponseMessage = new CreateTopicResponseMessage(getCreateTopicOperation().getQueue(),
                    this, getUserResponseMessage.getResult());

            send(getCreateTopicOperation().getRequester(), createTopicResponseMessage);
        }

        Miranda.getInstance().getTopicManager().sendCreateTopicMessage(getCreateTopicOperation().getQueue(), this,
                getCreateTopicOperation().getTopic());

        return getCreateTopicOperation().getCurrentState();
    }
}
