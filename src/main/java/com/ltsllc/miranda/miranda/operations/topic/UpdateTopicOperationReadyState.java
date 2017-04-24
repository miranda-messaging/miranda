package com.ltsllc.miranda.miranda.operations.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.user.messages.GetUserResponseMessage;

/**
 * Created by Clark on 4/23/2017.
 */
public class UpdateTopicOperationReadyState extends State {
    public UpdateTopicOperation getUpdateTopicOperation () {
        return (UpdateTopicOperation) getContainer();
    }

    public UpdateTopicOperationReadyState (UpdateTopicOperation updateTopicOperation) {
        super(updateTopicOperation);
    }

    public State processMessage (Message message) {
        State nextState = getUpdateTopicOperation().getCurrentState();

        switch(message.getSubject()) {
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

    public State processGetUserResponseMessage(GetUserResponseMessage getUserResponseMessage) {
        Miranda.getInstance().getTopicManager().sendUpdateTopicMessage(getUpdateTopicOperation().getQueue(),
                this, getUpdateTopicOperation().getTopic());

        return getUpdateTopicOperation().getCurrentState();
    }
}
