package com.ltsllc.miranda.operations.events;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.event.messages.NewEventMessage;
import com.ltsllc.miranda.event.messages.NewEventResponseMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.event.NewEventHolder;
import com.ltsllc.miranda.topics.Topic;
import com.ltsllc.miranda.topics.messages.GetTopicResponseMessage;
import com.ltsllc.miranda.user.User;

/**
 * The point in the event creation process where Miranda is verifying
 * the existence of the topic and the user's
 */
public class NewEventOperationVerifyingTopic extends OperationState {
    public NewEventOperationVerifyingTopic(NewEventOperation newEventOperation) {
        super(newEventOperation);
    }

    public NewEventOperation getNewEventOperation () {
        return (NewEventOperation) getContainer();
    }

    public State processMessage (Message message) {
        State nextState = getNewEventOperation().getCurrentState();

        switch (message.getSubject()) {
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

    public State start () {
        State nextState = getNewEventOperation().getCurrentState();

        //
        // in order to publish, a user must be an admin or a publisher
        //
        if (getNewEventOperation().getSession().getUser().getCategory() != User.UserTypes.Publisher &&
                getNewEventOperation().getSession().getUser().getCategory() != User.UserTypes.Admin) {
            reply(Results.NotPublisher);
            nextState = StopState.getInstance();
        }

        //
        // The user is a publisher or an admin, verify that the topic exists
        //
        Miranda.getInstance().getTopicManager().sendGetTopicMessage(getNewEventOperation().getQueue(), this,
                getNewEventOperation().getEvent().getTopicName());


        return nextState;
    }

    public State processGetTopicResponseMessage (GetTopicResponseMessage message) {
        State nextState = getOperation().getCurrentState();

        //
        // if the topic does not exist, tell the user and stop
        //
        if (message.getResult() == Results.TopicNotFound) {
            reply(message.getResult());
            nextState = StopState.getInstance();
        }

        //
        // otherwise, if the user doesn't own the topic and they are not an admin
        // then tell the user and stop
        //
        else if (!ownsTopic(message.getTopic()) && !isAdmin(())) {
            reply(Results.InsufficientPermissions);
            nextState = StopState.getInstance();
        }

        //
        // otherwise, the topic exists and the user either owns it or is an admin.
        // Tell the event manager about the new event.
        //
        Miranda.getInstance().getEventManager().createEvent (getNewEventOperation().getEvent());

        //
        // Tell the other Miranda nodes about the new event
        //
        tellNewEvent(getNewEventOperation().getEvent());

        //
        // if the remote policy for topic says to return immediately, then we are done.
        //
        if (message.getTopic().getRemotePolicy() == Topic.RemotePolicies.None) {
            reply(Results.Success);
            nextState = StopState.getInstance();
        }

        //
        // otherwise, wait for a quorum of the other nodes to respond
        //
        else {
            nextState = new NewEventOperationAwaitingQuorum(getNewEventOperation());
        }

        return nextState;
    }

    public boolean ownsTopic(Topic topic) {
        return getOperation().getSession().getUser().getName().equals(topic.getOwner());
    }

    public boolean isAdmin() {
        return getOperation().getSession().getUser().getCategory() == User.UserTypes.Admin;
    }

    public void tellNewEvent (Event event) {
        Miranda.getInstance().getCluster().sendStartConversation (getOperation().getQueue(), this);
        Miranda.getInstance().getCluster().broadcastNewEvent(getNewEventOperation().getQueue(), this, event);
    }
}
