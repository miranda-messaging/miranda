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

package com.ltsllc.miranda.servlet.topic;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Topic;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.ServletHolder;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/9/2017.
 */
public class TopicHolder extends ServletHolder {
    private static TopicHolder ourInstance;
    private static Logger logger = Logger.getLogger(TopicHolder.class);

    private List<Topic> topics;
    private Topic topic;
    private Results createResult;
    private Results updateResult;
    private Results deleteResult;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public List<Topic> getTopics() {
        if (null == topics)
            topics = new ArrayList<Topic>();

        return topics;
    }

    public Results getDeleteResult() {
        return deleteResult;
    }

    public void setDeleteResult(Results deleteResult) {
        this.deleteResult = deleteResult;
    }

    public Results getUpdateResult() {

        return updateResult;
    }

    public void setUpdateResult(Results updateResult) {
        this.updateResult = updateResult;
    }

    public Results getCreateResult() {
        return createResult;
    }

    public void setCreateResult(Results createResult) {
        this.createResult = createResult;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public static TopicHolder getInstance() {
        return ourInstance;
    }

    public static void setInstance(TopicHolder instance) {
        TopicHolder.ourInstance = instance;
    }

    public static synchronized void initialize(long timeout) throws MirandaException {
        if (null == ourInstance) {
            ourInstance = new TopicHolder(timeout);
        }
    }

    public TopicHolder(long timeout) throws MirandaException {
        super("topics holder", timeout);

        TopicHolderReadyState readyState = new TopicHolderReadyState(this);
        setCurrentState(readyState);
    }

    public List<Topic> getTopicList() throws TimeoutException {
        setTopics(null);
        Miranda.getInstance().getTopicManager().sendGetTopicsMessage(getQueue(), this);

        sleep();

        return getTopics();
    }

    public Results createTopic(Topic topic) throws TimeoutException {
        setCreateResult(Results.Unknown);
        Miranda.getInstance().getTopicManager().sendCreateTopicMessage(getQueue(), this, topic);

        sleep();

        return getCreateResult();
    }

    public Topic getTopic(String name) throws TimeoutException {
        setTopic(null);
        Miranda.getInstance().getTopicManager().sendGetTopicMessage(getQueue(), this, name);

        sleep();

        return getTopic();
    }

    public void setTopicAndAwaken(Topic topic) {
        setTopic(topic);
        wake();
    }

    public Results updateTopic(Topic topic) throws TimeoutException {
        setUpdateResult(Results.Unknown);
        Miranda.getInstance().sendUpdateTopicMessage(getQueue(), this, getSession(), topic);

        sleep();

        return getUpdateResult();
    }

    public Results deleteTopic(String topicName) throws TimeoutException {
        setDeleteResult(Results.Unknown);
        Miranda.getInstance().sendDeleteTopicMessage(getQueue(), this, getSession(), topicName);

        sleep();

        return getDeleteResult();
    }

    public void setUpdateResultAndAwaken(Results result) {
        setUpdateResult(result);
        wake();
    }

    public void setTopicsAndAwaken(List<Topic> topics) {
        List<Topic> newList = new ArrayList<Topic>(topics);
        setTopics(newList);
        wake();
    }

    public void setCreateResultAndAwaken(Results result) {
        setCreateResult(result);
        wake();
    }

    public void setDeleteResultAndAwaken(Results result) {
        setDeleteResult(result);
        wake();
    }
}
