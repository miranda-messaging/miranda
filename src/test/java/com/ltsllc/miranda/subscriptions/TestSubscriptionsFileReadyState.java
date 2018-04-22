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

package com.ltsllc.miranda.subscriptions;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.node.messages.GetFileMessage;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.subsciptions.SubscriptionsFileReadyState;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/24/2017.
 */
public class TestSubscriptionsFileReadyState extends TestCase {
    @Mock
    private SubscriptionsFile mockSubscriptionsFile;

    private SubscriptionsFileReadyState readyState;

    public SubscriptionsFileReadyState getReadyState() {
        return readyState;
    }

    @Override
    public SubscriptionsFile getMockSubscriptionsFile() {
        return mockSubscriptionsFile;
    }

    public void reset () throws Exception {
        super.reset();

        mockSubscriptionsFile = null;
        readyState = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        setuplog4j();

        mockSubscriptionsFile = mock(SubscriptionsFile.class);
        readyState = new SubscriptionsFileReadyState(mockSubscriptionsFile);
    }

    @Test
    public void testProcessGetVersionMessage () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        GetVersionMessage getVersionMessage = new GetVersionMessage(null, this, queue);

        Version version = new Version();
        when(getMockSubscriptionsFile().getVersion()).thenReturn(version);
        when(getMockSubscriptionsFile().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(getVersionMessage);

        assert (nextState instanceof SubscriptionsFileReadyState);
        assert (contains(Message.Subjects.Version, queue));
    }

    @Test
    public void testProcessGetFileMessage () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        GetFileMessage getFileMessage = new GetFileMessage(queue, this, "whatever");

        byte[] data = "whatever".getBytes();
        when(getMockSubscriptionsFile().getBytes()).thenReturn(data);

        State nextState = getReadyState().processMessage(getFileMessage);

        assert (nextState instanceof SubscriptionsFileReadyState);
        assert (contains(Message.Subjects.GetFileResponse, queue));
    }
}
