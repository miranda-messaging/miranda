package com.ltsllc.miranda.operations.topic;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.topics.messages.UpdateTopicResponseMessage;
import com.ltsllc.miranda.user.User;
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

            case UpdateTopicResponse: {
                UpdateTopicResponseMessage updateTopicResponseMessage = (UpdateTopicResponseMessage) message;
                nextState = processUpdateTopicResponseMessage(updateTopicResponseMessage);
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
        if (getUserResponseMessage.getResult() != Results.Success) {
            UpdateTopicResponseMessage updateTopicResponseMessage = new UpdateTopicResponseMessage(getUpdateTopicOperation().getQueue(),
                    this, getUserResponseMessage.getResult());

            send(getUpdateTopicOperation().getRequester(), updateTopicResponseMessage);

            return StopState.getInstance();
        }

        if (getUpdateTopicOperation().getSession().getUser().getName().equals(getUpdateTopicOperation().getTopic().getOwner())
            || getUpdateTopicOperation().getSession().getUser().getCategory() == User.UserTypes.Admin) {
            Miranda.getInstance().getTopicManager().sendUpdateTopicMessage(getUpdateTopicOperation().getQueue(),
                    this, getUpdateTopicOperation().getTopic());
        } else {
            UpdateTopicResponseMessage updateTopicResponseMessage = new UpdateTopicResponseMessage(getUpdateTopicOperation().getQueue(),
                    this, Results.NotOwner);

            send(getUpdateTopicOperation().getRequester(), updateTopicResponseMessage);

            return StopState.getInstance();
        }

        return getUpdateTopicOperation().getCurrentState();
    }

    public State processUpdateTopicResponseMessage (UpdateTopicResponseMessage updateTopicResponseMessage) {
        UpdateTopicResponseMessage responseMessage = new UpdateTopicResponseMessage(getUpdateTopicOperation().getQueue(),
                this, updateTopicResponseMessage.getResult());

        send(getUpdateTopicOperation().getRequester(), responseMessage);

        return StopState.getInstance();
    }
}
