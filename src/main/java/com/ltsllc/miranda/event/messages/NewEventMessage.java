package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/11/2017.
 */
public class NewEventMessage extends Message {
    private String guid;
    private String topicName;
    private Event.Methods method;
    private byte[] content;
    private Session session;

    public NewEventMessage (BlockingQueue<Message> senderQueue, Object sender, String guid, String topicName,
                            Event.Methods method, byte[] content, Session session) {
        super(Subjects.NewEvent, senderQueue, sender);

        this.session = session;
        this.guid = guid;
        this.topicName = topicName;
        this.method = method;
        this.content = content;
    }

    public Session getSession() {
        return session;
    }

    public byte[] getContent() {
        return content;
    }

    public Event.Methods getMethod() {
        return method;
    }

    public String getGuid() {
        return guid;
    }

    public String getTopicName() {
        return topicName;
    }
}
