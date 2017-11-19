package com.ltsllc.miranda.operations.events;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.Topic;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.event.messages.NewEventResponseMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.topics.messages.GetTopicResponseMessage;

/**
 * The point in the event creation process where Miranda is verifying
 * the existence of the topic and the user's
 */
public class NewEventOperationVerifyingTopic extends OperationState {
    private String conversation;
    private Cluster cluster;

    public NewEventOperationVerifyingTopic(NewEventOperation newEventOperation, Cluster cluster, String conversation) throws MirandaException {
        super(newEventOperation);

        this.conversation = conversation;
        this.cluster = cluster;
    }

    public String getConversation() {
        return conversation;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setConversationKey(String conversation) {
        this.conversation = conversation;
    }

    public NewEventOperation getNewEventOperation() {
        return (NewEventOperation) getContainer();
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getNewEventOperation().getCurrentState();

        switch (message.getSubject()) {
            case GetTopicResponse: {
                GetTopicResponseMessage getTopicResponseMessage = (GetTopicResponseMessage) message;
                nextState = processGetTopicResponseMessage(getTopicResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public boolean userIsPublisher() {
        return getNewEventOperation().getSession().getUser().getCategory() == User.UserTypes.Publisher;
    }

    public boolean userIsAdmin() {
        return getNewEventOperation().getSession().getUser().getCategory() == User.UserTypes.Admin;
    }

    public State start() {
        State nextState = getNewEventOperation().getCurrentState();

        setConversationKey(createConversationKey());

        //
        // in order to publish, a user must be an admin or a publisher
        //
        if (!userIsPublisher() && !userIsAdmin()) {
            reply(Results.NotPublisher);
            return StopState.getInstance();
        }

        //
        // The user is a publisher or an admin, verify that the topic exists
        //
        Miranda.getInstance().getTopicManager().sendGetTopicMessage(getNewEventOperation().getQueue(), this,
                getNewEventOperation().getEvent().getTopicName());


        return nextState;
    }

    public State processGetTopicResponseMessage(GetTopicResponseMessage message) throws MirandaException {
        State nextState = getOperation().getCurrentState();

        //
        // if the topic does not exist, tell the user and stop
        //
        if (message.getResult() == Results.TopicNotFound) {
            reply(message.getResult());
            return StopState.getInstance();
        }

        //
        // otherwise, if the user doesn't own the topic and they are not an admin
        // then tell the user and stop
        //
        else if (!ownsTopic(message.getTopic()) && !isAdmin()) {
            reply(Results.NotOwner);
            return StopState.getInstance();
        }

        //
        // otherwise, the topic exists and the user either owns it or is an admin.
        // Tell the event manager about the new event.
        //
        Miranda.getInstance().getEventManager().createEvent(getNewEventOperation().getEvent());

        //
        // Tell the other Miranda nodes about the new event
        //
        tellNewEvent(getNewEventOperation().getEvent());

        //
        // if the remote policy for topic says to return immediately, then we do so
        //
        if (message.getTopic().getRemotePolicy() == Topic.RemotePolicies.Immediate) {
            reply(Results.Success);
            nextState = StopState.getInstance();
        }

        //
        // otherwise, wait for a quorum of the other nodes to respond
        //
        else {
            Quorum quorum = createQuorum(message.getTopic());
            nextState = new NewEventOperationAwaitingQuorum(getNewEventOperation(), quorum);
        }

        return nextState;
    }

    public Quorum createQuorum(Topic topic) {
        if (topic.getRemotePolicy() == Topic.RemotePolicies.Acknowledged)
            return getCluster().createAcknowledgeQuorum();
        else if (topic.getRemotePolicy() == Topic.RemotePolicies.Written)
            return getCluster().createWriteQuorum();
        else {
            Panic panic = new Panic("Unrecognized remote policy: " + topic.getRemotePolicy().toString(),
                    Panic.Reasons.UnrecognizedRemotePolicy);
            Miranda.panicMiranda(panic);
            return null;
        }
    }

    public boolean ownsTopic(Topic topic) {
        return getOperation().getSession().getUser().getName().equals(topic.getOwner());
    }

    public boolean isAdmin() {
        return getOperation().getSession().getUser().getCategory() == User.UserTypes.Admin;
    }

    public void tellNewEvent(Event event) {
        setConversationKey(createConversationKey());
        Miranda.getInstance().getCluster().sendStartConversationMessage(getOperation().getQueue(), this,
                getConversation(), getOperation().getQueue());

        Miranda.getInstance().getCluster().sendBroadcastNewEventMessage(getNewEventOperation().getQueue(), this,
                getConversation(), event);
    }

    public Message createResponseMessage(Results result) {
        return new NewEventResponseMessage(getNewEventOperation().getQueue(), this, result,
                getNewEventOperation().getEvent());
    }
}
