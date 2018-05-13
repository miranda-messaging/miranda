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

package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.network.messages.ConnectToMessage;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
import com.ltsllc.miranda.network.states.NetworkReadyState;
import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/20/2017.
 */
public class TestNetworkReadyState extends TestCase {
    private NetworkReadyState networkReadyState;

    public NetworkReadyState getNetworkReadyState() {
        return networkReadyState;
    }

    public void reset () throws Exception {
        super.reset();

        networkReadyState = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        setuplog4j();

        networkReadyState = new NetworkReadyState(getMockNetwork());
    }

    @Test
    public void testProcessConnectToMessage () throws MirandaException {
        ConnectToMessage connectToMessage = new ConnectToMessage("foo.com", 6789, null, this);

        getNetworkReadyState().processMessage(connectToMessage);

        verify(getMockNetwork(), atLeastOnce()).connect(Matchers.any(ConnectToMessage.class));
    }

    @Test
    public void testProcessSendNetworkMessageSuccess () throws MirandaException {
        JoinWireMessage joinWireMessage = new JoinWireMessage("foo.com", "192.168.1.1", 6789, "a node");
        SendNetworkMessage sendNetworkMessage = new SendNetworkMessage(null, this, joinWireMessage, -1);

        getNetworkReadyState().processMessage(sendNetworkMessage);

        try {
            verify(getMockNetwork(), atLeastOnce()).sendOnNetwork(Matchers.any(SendNetworkMessage.class));
        } catch (NetworkException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Test
    public void testProcessSendNetworkMessageExceptionUnrecognizedHandle () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        JoinWireMessage joinWireMessage = new JoinWireMessage("foo.com", "192.168.1.1", 6789, "a node");
        SendNetworkMessage sendNetworkMessage = new SendNetworkMessage(queue, this, joinWireMessage, -1);
        NetworkException networkException = new NetworkException("Test", NetworkException.Errors.UnrecognizedHandle);

        try {
            doThrow(networkException).when(getMockNetwork()).sendOnNetwork(Matchers.any(SendNetworkMessage.class));
        } catch (NetworkException e) {
            e.printStackTrace();
            System.exit(1);
        }

        getNetworkReadyState().processMessage(sendNetworkMessage);

        try {
            verify(getMockNetwork(), atLeastOnce()).sendOnNetwork(Matchers.any(SendNetworkMessage.class));
        } catch (NetworkException e) {
            e.printStackTrace();
            System.exit(1);
        }

        assert (contains(Message.Subjects.UnknownHandle, queue));
    }

    @Test
    public void testProcessSendNetworkMessageException () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        JoinWireMessage joinWireMessage = new JoinWireMessage("foo.com", "192.168.1.1", 6789, "a node");
        SendNetworkMessage sendNetworkMessage = new SendNetworkMessage(queue, this, joinWireMessage, -1);
        NetworkException networkException = new NetworkException("Test", NetworkException.Errors.ExceptionSending);

        try {
            doThrow(networkException).when(getMockNetwork()).sendOnNetwork(Matchers.any(SendNetworkMessage.class));
        } catch (NetworkException e) {
            e.printStackTrace();
            System.exit(1);
        }

        getNetworkReadyState().processMessage(sendNetworkMessage);

        try {
            verify(getMockNetwork(), atLeastOnce()).sendOnNetwork(Matchers.any(SendNetworkMessage.class));
        } catch (NetworkException e) {
            e.printStackTrace();
            System.exit(1);
        }

        assert (contains(Message.Subjects.NetworkError, queue));

    }
}
