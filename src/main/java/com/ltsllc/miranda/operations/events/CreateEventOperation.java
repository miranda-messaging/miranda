package com.ltsllc.miranda.operations.events;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.event.EventManager;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.topics.TopicManager;

import java.util.concurrent.BlockingQueue;

/**
 * An operation that creates a new event
 */
public class CreateEventOperation extends Operation {
    public static final String NAME = "create event operation";

    private EventManager eventManager;
    private TopicManager topicManager;
    private Cluster cluster;
    private Event event;

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public TopicManager getTopicManager() {
        return topicManager;
    }

    public void setTopicManager(TopicManager topicManager) {
        this.topicManager = topicManager;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public CreateEventOperation (EventManager eventManager, TopicManager topicManager, Cluster cluster, Session session,
                                 BlockingQueue<Message> senderQueue, Event event) throws MirandaException {
        super(NAME, senderQueue, session);

        setEventManager(eventManager);
        setTopicManager(topicManager);
        setCluster(cluster);
        setEvent(event);
        CreateEventOperationStartState createEventOperationStartState = new CreateEventOperationStartState(this);
        setCurrentState(createEventOperationStartState);
    }
}
