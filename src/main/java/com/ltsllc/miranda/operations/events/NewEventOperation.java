package com.ltsllc.miranda.operations.events;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.event.EventManager;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.topics.TopicManager;

import java.util.concurrent.BlockingQueue;

/**
 * Create a new event and distribute it to the Cluster.
 * <p>
 * This class represents the knowledge of how to create a new Miranda event.
 * This consists of
 * <ul>
 * <li>Ensuring that the topic exists.</li>
 * <li>Ensuring that the user can create event for that topic.</li>
 * <li>If the topic's remote policy warrants it, waiting for a qourum of the other nodes to acknowledge the new event.</li>
 * <li>If the topic's remote policy warrants it, waiting for a quorum of the other nodes to record the new event.</li>
 * <li>Responding to the publisher</li>
 * </ul>
 */
public class NewEventOperation extends Operation {
    public static final String NAME = "new event operation";

    private EventManager eventManager;
    private TopicManager topicManager;
    private Cluster cluster;
    private Event event;

    public NewEventOperation(EventManager eventManager, TopicManager topicManager, Cluster cluster, Session session,
                             BlockingQueue<Message> requester, Event event) throws MirandaException {

        super(NAME, requester, session);

        this.eventManager = eventManager;
        this.topicManager = topicManager;
        this.cluster = cluster;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public TopicManager getTopicManager() {
        return topicManager;
    }

    public Cluster getCluster() {
        return cluster;
    }
}
