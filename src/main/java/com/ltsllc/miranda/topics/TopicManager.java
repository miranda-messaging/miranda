package com.ltsllc.miranda.topics;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Manager;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.subsciptions.messages.OwnerQueryMessage;
import com.ltsllc.miranda.topics.messages.*;
import com.ltsllc.miranda.topics.states.TopicManagerReadyState;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class TopicManager extends Manager<Topic, Topic> {
    public static final String NAME = "TopicManager";

    private static Logger logger = Logger.getLogger(TopicManager.class);

    public TopicsFile getTopicsFile() {
        return (TopicsFile) getFile();
    }

    public List<Topic> getTopics() {
        return getData();
    }

    public TopicManager (String filename) {
        super("topics manager", new TopicsFile(filename, Miranda.getInstance().getWriter()));

        TopicManagerReadyState topicManagerReadyState = new TopicManagerReadyState(this);
        setCurrentState(topicManagerReadyState);
    }

    public void sendGetTopicsMessage (BlockingQueue<Message> senderQueue, Object sender) {
        GetTopicsMessage getTopicsMessage = new GetTopicsMessage(senderQueue, sender);
        sendToMe(getTopicsMessage);
    }

    public void performGarbageCollection () {
        long now = System.currentTimeMillis();

        List<Topic> expired = new ArrayList<Topic>();

        for (Topic topic : getTopics()) {
            if (topic.expired(now))
                expired.add(topic);
        }

        for (Topic topic : expired) {
            logger.info ("Removing expired topic " + topic.getName());
        }

        getTopics().removeAll(expired);
    }

    public Topic getTopic (String name) {
        for (Topic topic : getTopics()) {
            if (topic.getName().equals(name))
                return topic;
        }

        return null;
    }

    public boolean contains (String name) {
        return getTopic(name) != null;
    }

    public void addTopic (Topic topic) throws DuplicateTopicException {
        if (contains(topic.getName())) {
            throw new DuplicateTopicException("Duplicate topic named: " + topic.getName());
        } else {
            getTopics().add(topic);

            List<Topic> topics = new ArrayList<Topic>();
            topics.add(topic);
            getTopicsFile().sendAddObjectsMessage(getQueue(), this, topics);
        }
    }

    public boolean deleteTopic (String name) {
        boolean deleted = false;

        Topic topic = getTopic(name);
        if (null != topic) {
            getTopics().remove(topic);
            getTopicsFile().sendRemoveObjectsMessage(getQueue(), this, topic);
            deleted = true;
        }

        return deleted;
    }

    public void setTopics (List<Topic> topics) {
        setData(topics);
    }

    public void sendCreateTopicMessage (BlockingQueue<Message> senderQueue, Object sender, Topic topic) {
        CreateTopicMessage createTopicMessage = new CreateTopicMessage(senderQueue, sender, null, topic);
        sendToMe(createTopicMessage);
    }

    public void sendUpdateTopicMessage (BlockingQueue<Message> senderQueue, Object sender, Topic topic) {
        UpdateTopicMessage updateTopicMessage = new UpdateTopicMessage(senderQueue, sender, null, topic);
        sendToMe(updateTopicMessage);
    }

    public void sendDeleteTopicMessage (BlockingQueue<Message> senderQueue, Object sender, String topicName) {
        DeleteTopicMessage deleteTopicMessage = new DeleteTopicMessage(senderQueue, sender, null, topicName);
        sendToMe(deleteTopicMessage);
    }

    public void updateTopic (Topic topic) throws TopicNotFoundException {
        Topic existingTopic = getTopic(topic.getName());

        if (null == existingTopic)
            throw new TopicNotFoundException ("Could not find " + topic.getName());
        else {
            existingTopic.updateFrom(topic);

            getTopicsFile().sendUpdateObjectsMessage(getQueue(), this, topic);
        }
    }

    public void deleteTopic (Topic topic) {
        Topic existingTopic = getTopic(topic.getName());
        if (null != existingTopic) {
            getTopics().remove(existingTopic);

            List<Topic> deletedTopics = new ArrayList<Topic>();
            deletedTopics.add(topic);
            getTopicsFile().sendRemoveObjectsMessage(getQueue(), this, deletedTopics);
        }
    }

    public void sendGetTopicMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        GetTopicMessage getTopicMessage = new GetTopicMessage(senderQueue, sender, name);
        sendToMe(getTopicMessage);
    }

    public void sendOwnerQueryMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        OwnerQueryMessage ownerQueryMessage = new OwnerQueryMessage(senderQueue, sender, name);
        sendToMe(ownerQueryMessage);
    }

    public List<String> getPropertyOf (String name) {
        List<String> property = new ArrayList<String>();
        for (Topic topic : getTopics()) {
            if (topic.getOwner().equals(name)) {
                property.add(topic.getName());
            }
        }

        return property;
    }

    public void sendGarbageCollectionMessage (BlockingQueue<Message> senderQueue, Object sender) {
        GarbageCollectionMessage garbageCollectionMessage = new GarbageCollectionMessage(senderQueue, sender);
        sendToMe(garbageCollectionMessage);
    }

    public Topic convert (Topic topic) {
        return topic;
    }
}
