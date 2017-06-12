package com.ltsllc.miranda.operations.events;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.event.messages.NewEventMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.session.Session;

/**
 * Create a new event and distribute it to the Cluster.
 *
 * This class represents the knowledge of how to create a new Miranda event.
 * This consists of
 * <ul>
 *     <li>Ensuring that the topic exists.</li>
 *     <li>Ensuring that the user can create event for that topic.</li>
 *     <li>If the topic's remote policy warrants it, waiting for a qourum of the other nodes to acknowledge the new event.</li>
 *     <li>If the topic's remote policy warrants it, waiting for a quorum of the other nodes to record the new event.</li>
 *     <li>Responding to the publisher</li>
 * </ul>
 */
public class NewEventOperation extends Consumer {
    private Event event;
    private Session session;
    private NewEventMessage originalMessage;

    public NewEventOperation (NewEventMessage newEventMessage, Session session, String guid, String topicName,
                              Event.Methods method, byte[] content) {
        //
        // get info on the topic
        //
        Miranda.getInstance().getTopicManager().sendGetTopicMessage(getQueue(), this, topicName);

        //
        // in the mean time, create the event
        //
        Event event = new Event(session.getUser().getName(), guid, topicName, System.currentTimeMillis(), method,
                content);

        setEvent (event);
        setSession(session);
        setOriginalMessage(newEventMessage);
    }

    public NewEventMessage getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(NewEventMessage originalMessage) {
        this.originalMessage = originalMessage;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
