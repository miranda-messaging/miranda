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
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.topics.states.TopicsFileReadyState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.mock;

/**
 * Created by Clark on 3/24/2017.
 */
public class TestTopicsFileReadyState extends TestCase {
    @Mock
    private TopicsFile mockTopicsFile;

    private TopicsFileReadyState readyState;

    public TopicsFileReadyState getReadyState() {
        return readyState;
    }

    @Override
    public TopicsFile getMockTopicsFile() {
        return mockTopicsFile;
    }

    public void reset () {
        super.reset();

        mockTopicsFile = null;
        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockTopicsFile = mock(TopicsFile.class);
        readyState = new TopicsFileReadyState(mockTopicsFile);
    }

    @Test
    public void testProcessGetVersion () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        GetVersionMessage getVersionMessage = new GetVersionMessage(null, this, queue);

        State nextState = getReadyState().processMessage(getVersionMessage);

        assert (nextState instanceof TopicsFileReadyState);
        assert (contains(Message.Subjects.Version, queue));
    }
}
