/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.topics;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.basicclasses.Topic;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.manager.StandardManager;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.subsciptions.messages.OwnerQueryMessage;
import com.ltsllc.miranda.topics.messages.*;
import com.ltsllc.miranda.topics.states.TopicManagerStartState;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class TopicManager extends StandardManager <Topic> {
    public static final String NAME = "TopicManager";

    private static Logger logger = Logger.getLogger(TopicManager.class);

    public TopicsFile getTopicsFile() {
        return (TopicsFile) getFile();
    }

    public List<Topic> getTopics() {
        return getData();
    }

    public TopicManager (String filename) throws IOException {
        super("topics manager", filename);

        TopicManagerStartState topicManagerStartState = new TopicManagerStartState(this);
        setCurrentState(topicManagerStartState);
    }

    public SingleFile<Topic> createFile (String filename) throws IOException {
        return new TopicsFile(filename, Miranda.getInstance().getReader(), Miranda.getInstance().getWriter());
    }

    public State createStartState () {
        return new TopicManagerStartState(this);
    }

    public void sendGetTopicsMessage (BlockingQueue<Message> senderQueue, Object sender) {
        ListTopicsMessage getTopicsMessage = new ListTopicsMessage(senderQueue, sender);
        sendToMe(getTopicsMessage);
    }

    public void performGarbageCollection () {
        logger.info ("performGarbageCollection called");
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

    public Topic convert (Topic topic) {
        return topic;
    }
}
