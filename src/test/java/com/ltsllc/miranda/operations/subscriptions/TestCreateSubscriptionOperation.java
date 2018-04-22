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

package com.ltsllc.miranda.operations.subscriptions;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 5/16/2017.
 */
public class TestCreateSubscriptionOperation extends TestCase {
    private CreateSubscriptionOperation operation;
    private BlockingQueue<Message> queue;

    @Mock
    private Subscription mockSubscription;

    public CreateSubscriptionOperation getOperation() {
        return operation;
    }

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    public Subscription getMockSubscription() {
        return mockSubscription;
    }

    public void reset () throws Exception {
        super.reset();

        mockSubscription = null;
        queue = null;
        operation = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        setuplog4j();
        mockSubscription = mock(Subscription.class);
        queue = new LinkedBlockingQueue<Message>();
        operation = new CreateSubscriptionOperation(getQueue(), getMockSession(), getMockSubscription());
    }

    @Test
    public void testConstructor () {
        assert (getOperation().getRequester() == getQueue());
        assert (getOperation().getCurrentState() instanceof CreateSubscriptionOperationReadyState);
        assert (getOperation().getSubscription() == getMockSubscription());
    }

    @Test
    public void testStart () {
        setupMockMiranda();

        when(getMockMiranda().getUserManager()).thenReturn(getMockUserManager());
        when(getMockMiranda().getTopicManager()).thenReturn(getMockTopicManager());

        getOperation().start();

        verify(getMockUserManager(), atLeastOnce()).sendGetUser(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyString());
        verify(getMockTopicManager(), atLeastOnce()).sendGetTopicMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.anyString());
    }
}
