package com.ltsllc.miranda.miranda.operations.subscriptions;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.subsciptions.messages.UpdateSubscriptionResponseMessage;
import com.ltsllc.miranda.topics.messages.GetTopicResponseMessage;
import com.ltsllc.miranda.topics.messages.GetTopicsResponseMessage;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.user.messages.GetUserResponseMessage;

/**
 * Created by Clark on 4/22/2017.
 */
public class UpdateSubscriptionOperationReadyState extends State {
    public UpdateSubscriptionOperation getUpdateSubscriptionOperation () {
        return (UpdateSubscriptionOperation) getContainer();
    }

    public UpdateSubscriptionOperationReadyState (UpdateSubscriptionOperation updateSubscriptionOperation) {
        super(updateSubscriptionOperation);
    }

    public State processMessage (Message message) {
        State nextState = getUpdateSubscriptionOperation().getCurrentState();

        switch (message.getSubject()) {
            case UpdateSubscriptionResponse: {
                UpdateSubscriptionResponseMessage updateSubscriptionResponseMessage = (UpdateSubscriptionResponseMessage)
                        message;

                nextState = processUpdateSubscriptionResponseMessage (updateSubscriptionResponseMessage);
                break;
            }

            case GetUserResponse: {
                GetUserResponseMessage getUserResponseMessage = (GetUserResponseMessage) message;
                nextState = processGetUserResponseMessage(getUserResponseMessage);
                break;
            }

            case GetTopicResponse: {
                GetTopicResponseMessage getTopicResponseMessage = (GetTopicResponseMessage) message;
                nextState = processGetTopicResponseMessage (getTopicResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processUpdateSubscriptionResponseMessage(UpdateSubscriptionResponseMessage updateSubscriptionResponseMessage) {
        if (updateSubscriptionResponseMessage.getResult() == Results.Success) {
            Miranda.getInstance().getCluster().sendUpdateSubscriptionMessage (getUpdateSubscriptionOperation().getQueue(),
                    this, getUpdateSubscriptionOperation().getSession(), getUpdateSubscriptionOperation().getSubscription());
        }

        UpdateSubscriptionResponseMessage updateSubscriptionResponseMessage2 = new UpdateSubscriptionResponseMessage(
                getUpdateSubscriptionOperation().getQueue(), this, updateSubscriptionResponseMessage.getResult()
        );

        send(getUpdateSubscriptionOperation().getRequester(), updateSubscriptionResponseMessage2);

        return StopState.getInstance();
    }

    public State processGetUserResponseMessage (GetUserResponseMessage getUserResponseMessage) {
        if (getUserResponseMessage.getResult() != Results.Success) {
            UpdateSubscriptionResponseMessage response = new UpdateSubscriptionResponseMessage(getUpdateSubscriptionOperation().getQueue(),
                    this, Results.UserNotFound);

            send(getUpdateSubscriptionOperation().getRequester(), response);

            return StopState.getInstance();
        }

        if (!getUpdateSubscriptionOperation().getSubscription().getOwner().equals(getUpdateSubscriptionOperation().getSession().getUser().getName()) &&
                getUpdateSubscriptionOperation().getSession().getUser().getCategory() != User.UserTypes.Admin)
        {
            UpdateSubscriptionResponseMessage response = new UpdateSubscriptionResponseMessage(getUpdateSubscriptionOperation().getQueue(),
                    this, Results.NotOwner);

            send(getUpdateSubscriptionOperation().getRequester(), getUserResponseMessage);

            return StopState.getInstance();
        }

        getUpdateSubscriptionOperation().setUserManagerResponded(true);

        if (getUpdateSubscriptionOperation().getTopicManagerResponded()) {
            Miranda.getInstance().getSubscriptionManager().sendUpdateSubscriptionMessage(getUpdateSubscriptionOperation().getQueue(),
                    this, getUpdateSubscriptionOperation().getSession(),
                    getUpdateSubscriptionOperation().getSubscription());
        }

        return getUpdateSubscriptionOperation().getCurrentState();
    }

    public State processGetTopicResponseMessage (GetTopicResponseMessage getTopicResponseMessage) {
        if (getTopicResponseMessage.getTopic() == null) {
            UpdateSubscriptionResponseMessage response = new UpdateSubscriptionResponseMessage(getUpdateSubscriptionOperation().getQueue(),
                    this, Results.TopicNotFound);

            send(getUpdateSubscriptionOperation().getRequester(), response);

            return StopState.getInstance();
        }

        getUpdateSubscriptionOperation().setTopicManagerResponded(true);

        if (getUpdateSubscriptionOperation().getUserManagerResponded()) {
            Miranda.getInstance().getSubscriptionManager().sendUpdateSubscriptionMessage(getUpdateSubscriptionOperation().getQueue(),
                    this, getUpdateSubscriptionOperation().getSession(),
                    getUpdateSubscriptionOperation().getSubscription());
        }

        return getUpdateSubscriptionOperation().getCurrentState();
    }
}
