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

package com.ltsllc.miranda.servlet.subscription;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.subsciptions.messages.*;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestSubscriptionHolderReadyState extends TestCase {
    @Mock
    private SubscriptionHolder mockSubscriptionHolder;

    @Mock
    private Subscription mockSubscription;

    private SubscriptionHolderReadyState readyState;

    public SubscriptionHolderReadyState getReadyState() {
        return readyState;
    }

    public SubscriptionHolder getMockSubscriptionHolder() {
        return mockSubscriptionHolder;
    }

    public Subscription getMockSubscription() {
        return mockSubscription;
    }

    public void reset () throws MirandaException {
        super.reset();

        mockSubscription = null;
        mockSubscriptionHolder = null;
        readyState = null;
    }

    @Before
    public void setup () throws MirandaException {
        reset();

        super.setup();

        mockSubscription = mock(Subscription.class);
        mockSubscriptionHolder = mock(SubscriptionHolder.class);
        readyState = new SubscriptionHolderReadyState(mockSubscriptionHolder);
    }

    @Test
    public void testProcessGetSubscriptionResponseMessage () throws MirandaException {
        GetSubscriptionResponseMessage getSubscriptionsResponseMessage = new GetSubscriptionResponseMessage(null,
                this, Results.Success, getMockSubscription());

        when(getMockSubscriptionHolder().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(getSubscriptionsResponseMessage);

        assert (nextState == getReadyState());
        verify(getMockSubscriptionHolder(), atLeastOnce()).setSubscriptionAndAwaken(Matchers.eq(Results.Success),
                Matchers.any(Subscription.class));
    }

    @Test
    public void testProcessGetSubscriptionsResponseMessage () throws MirandaException {
        GetSubscriptionsResponseMessage getSubscriptionsResponseMessage = new GetSubscriptionsResponseMessage(null,
                this, new ArrayList<Subscription>());
        when(getMockSubscriptionHolder().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(getSubscriptionsResponseMessage);

        assert (nextState == getReadyState());
        verify(getMockSubscriptionHolder(), atLeastOnce()).setSubscriptionsAndAwaken(Matchers.anyList());
    }

    @Test
    public void testProcessCreateSubscriptionResponseMessage () throws MirandaException {
        CreateSubscriptionResponseMessage responseMessage = new CreateSubscriptionResponseMessage(null, this,
                Results.Success);

        when(getMockSubscriptionHolder().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(responseMessage);

        assert (nextState == getReadyState());
        verify(getMockSubscriptionHolder(), atLeastOnce()).setCreateResultAndAwaken(Matchers.any(Results.class));
    }

    @Test
    public void testProcessUpdateSubscriptionResponseMessage () throws MirandaException {
        UpdateSubscriptionResponseMessage response = new UpdateSubscriptionResponseMessage(null,this,
                Results.Success);

        when(getMockSubscriptionHolder().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(response);

        assert (nextState == getReadyState());
        verify(getMockSubscriptionHolder(), atLeastOnce()).setUpdateResultAndAwaken(Matchers.eq(Results.Success));
    }

    @Test
    public void testProcessDeleteSubscriptionResponseMessage () throws MirandaException {
        DeleteSubscriptionResponseMessage response = new DeleteSubscriptionResponseMessage(null, this,
                Results.Success);

        when(getMockSubscriptionHolder().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(response);

        assert (nextState == getReadyState());
        verify(getMockSubscriptionHolder(), atLeastOnce()).setDeleteResultAndAwaken(Matchers.eq(Results.Success));
    }


}
