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

package com.ltsllc.miranda.servlet.holder;

import com.ltsllc.common.util.ImprovedRandom;
import com.ltsllc.miranda.clientinterface.basicclasses.Topic;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.servlet.topic.TopicHolder;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 4/15/2017.
 */
public class TestTopicHolder extends TestCase {
    public static class LocalRunner implements Runnable {
        public void run () {

        }
    }

    private TopicHolder topicHolder;

    public TopicHolder getTopicHolder() {
        return topicHolder;
    }

    public void reset () {
        super.reset();

        topicHolder = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        topicHolder = new TopicHolder(1000);
    }

    @Test
    public void testGetTopicListTimeout () {
        setupMockMiranda();
        when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());

        TimeoutException timeoutException = null;

        try {
            List<Topic> topicList = getTopicHolder().getTopicList();
        } catch (TimeoutException e) {
            timeoutException = e;
        }

        assert (timeoutException != null);
        verify(getMockTopicManager(), atLeastOnce()).sendGetTopicsMessage(Matchers.any(BlockingQueue.class),
                Matchers.any());
    }

    public static class TestRunner implements Runnable {
        public List<Topic> topicList;
        public TestTopicHolder testTopicHolder;

        public TestRunner (TestTopicHolder testTopicHolder) {
            this.testTopicHolder = testTopicHolder;
        }

        public void run () {
            try {
                topicList = testTopicHolder.getTopicHolder().getTopicList();
            } catch (TimeoutException e) {

            }
        }
    }

    @Test
    public void testGetTopicListSuccess () {
        setupMockMiranda();
        when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());

        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        Topic topic = Topic.random(improvedRandom);
        List<Topic> returnedList = new ArrayList<Topic>();
        returnedList.add(topic);

        TestRunner testRunner = new TestRunner(this);
        Thread thread = new Thread(testRunner);
        thread.start();

        pause(100);

        getTopicHolder().setTopicsAndAwaken(returnedList);

        pause(100);

        List<Topic> topicList = testRunner.topicList;

        assert (topicList.size() > 0);
        verify(getMockTopicManager(), atLeastOnce()).sendGetTopicsMessage(Matchers.any(BlockingQueue.class),
                Matchers.any());
    }

    public static class CreateRunner implements Runnable {
        private TestTopicHolder testTopicHolder;
        public TimeoutException timeoutException;
        public Results result;

        public TestTopicHolder getTestTopicHolder() {
            return testTopicHolder;
        }

        public CreateRunner (TestTopicHolder testTopicHolder) {
            this.testTopicHolder = testTopicHolder;
        }

        public void run () {
            Topic topic = new Topic ("Whatever", "whatever");

            try {
                result = testTopicHolder.getTopicHolder().createTopic(topic);
            } catch (TimeoutException e) {
                timeoutException = e;
                result = Results.Timeout;
            }
        }
    }

    @Test
    public void testCreateTopicTimeout () {
        setupMockMiranda();

        when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());

        CreateRunner createRunner = new CreateRunner(this);
        Thread thread = new Thread(createRunner);
        thread.start();

        pause(1500);

        assert (createRunner.timeoutException != null);
    }

    @Test
    public void testCreateTopicUserNotFound () {
        setupMockMiranda();

        when (getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());

        CreateRunner createRunner = new CreateRunner(this);
        Thread thread = new Thread(createRunner);
        thread.start();

        pause(100);

        getTopicHolder().setCreateResultAndAwaken(Results.UserNotFound);

        pause (100);

        assert (createRunner.result == Results.UserNotFound);
    }

    @Test
    public void testCreateTopicPermissions () {
        setupMockMiranda();

        when (getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());

        CreateRunner createRunner = new CreateRunner(this);
        Thread thread = new Thread(createRunner);
        thread.start();

        pause(100);

        getTopicHolder().setCreateResultAndAwaken(Results.InsufficientPermissions);

        pause (100);

        assert (createRunner.result == Results.InsufficientPermissions);
    }

    @Test
    public void testCreateTopicSuccess () {
        setupMockMiranda();

        when (getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());

        CreateRunner createRunner = new CreateRunner(this);
        Thread thread = new Thread(createRunner);
        thread.start();

        pause(100);

        getTopicHolder().setCreateResultAndAwaken(Results.Success);

        pause (100);

        assert (createRunner.result == Results.Success);
    }

    @Test
    public void testUpdateTopicTimeout () {
        setupMockMiranda();

        when (getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());

        CreateRunner createRunner = new CreateRunner(this);
        Thread thread = new Thread(createRunner);
        thread.start();

        pause(1500);

        assert (createRunner.result == Results.Timeout);
    }
}
