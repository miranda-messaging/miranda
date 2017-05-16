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
import com.ltsllc.miranda.test.ServletHolderRunner;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.topics.Topic;
import com.ltsllc.miranda.topics.states.TopicManagerReadyState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 5/16/2017.
 */
public class TestTopicHolder extends TestCase {
    public enum Methods {
        CreateTopic,
        GetTopics
    }

    public static class TopicRunner extends ServletHolderRunner {
        private Methods method;
        private Results result;
        private Topic topic;

        public Topic getTopic() {
            return topic;
        }

        public void setTopic(Topic topic) {
            this.topic = topic;
        }

        public Results getResult() {
            return result;
        }

        public void setResult(Results result) {
            this.result = result;
        }

        public TopicHolder getTopicHolder () {
            return (TopicHolder) getServletHolder();
        }

        public Methods getMethod() {
            return method;
        }

        public void setMethod(Methods method) {
            this.method = method;
        }

        public TopicRunner (TopicHolder topicHolder, Methods method) {
            super(topicHolder);

            this.method = method;
        }

        public TopicRunner (TopicHolder topicHolder, Methods method, Topic topic) {
            super(topicHolder);

            this.method = method;
            this.topic = topic;
        }

        public void run () {
            try {
                basicRun();
            } catch (TimeoutException e) {
                setResult(Results.Timeout);
            }
        }

        public void basicRun () throws TimeoutException {
            switch (getMethod()) {
                case GetTopics: {
                    getTopicHolder().getTopicList();
                    setResult(Results.Success);
                    break;
                }

                case CreateTopic: {
                    getTopicHolder().createTopic(getTopic());
                    setResult(getTopicHolder().getCreateResult());
                    break;
                }

                default: {
                    setResult(Results.UnrecognizedMethod);
                    break;
                }
            }
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
        setupMockMiranda();
        topicHolder = new TopicHolder(500);
    }

    @Test
    public void testConstructor () {
        assert (500 == getTopicHolder().getTimeoutPeriod());
        assert (getTopicHolder().getCurrentState() instanceof TopicHolderReadyState);
    }

    @Test
    public void testGetTopicsSuccess () {
        when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());

        TopicRunner topicRunner = new TopicRunner(getTopicHolder(), Methods.GetTopics);
        topicRunner.start();

        pause(50);

        getTopicHolder().setTopicsAndAwaken(new ArrayList<Topic>());

        pause(50);

        assert (topicRunner.getResult() == Results.Success);
    }

    @Test
    public void testGetTopicsTimeout () {
        when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());

        TopicRunner topicRunner = new TopicRunner(getTopicHolder(), Methods.GetTopics);
        topicRunner.start();

        pause (1000);

        assert(topicRunner.getResult() == Results.Timeout);
    }

    @Test
    public void testCreateTopicSuccess () {
        when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());

        Topic topic = new Topic("whatever", "whatever");
        TopicRunner topicRunner = new TopicRunner(getTopicHolder(), Methods.CreateTopic, topic);
        topicRunner.start();

        pause(50);

        getTopicHolder().setCreateResultAndAwaken(Results.Success);

        pause(50);

        assert (topicRunner.getResult() == Results.Success);
        verify(getMockTopicManager(), atLeastOnce()).sendCreateTopicMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.any(Topic.class));
    }

    @Test
    public void testCreateTopicTimeout () {
        when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());

        Topic topic = new Topic("whatever", "whatever");
        TopicRunner topicRunner = new TopicRunner(getTopicHolder(), Methods.CreateTopic, topic);
        topicRunner.start();

        pause(1000);

        assert (topicRunner.getResult() == Results.Timeout);
    }
}
