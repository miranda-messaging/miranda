package com.ltsllc.miranda.servlet.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.servlet.objects.Property;
import com.ltsllc.miranda.servlet.objects.StatusObject;
import com.ltsllc.miranda.servlet.StatusServlet;
import com.ltsllc.miranda.servlet.messages.GetStatusResponseMessage;
import com.ltsllc.miranda.servlet.objects.MirandaStatus;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 3/24/2017.
 */
public class TestMirandaStatusReadyState extends TestCase {
    @Mock
    private MirandaStatus mockMirandaStatus;

    @Mock
    private StatusServlet mockStatusServlet;

    private MirandaStatusReadyState readyState;

    public MirandaStatusReadyState getReadyState() {
        return readyState;
    }

    public StatusServlet getMockStatusServlet() {
        return mockStatusServlet;
    }

    public MirandaStatus getMockMirandaStatus() {
        return mockMirandaStatus;
    }

    public void reset () {
        super.reset();

        mockMirandaStatus = null;
        mockStatusServlet = null;
        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        mockMirandaStatus = mock(MirandaStatus.class);
        mockStatusServlet = mock(StatusServlet.class);
        readyState = new MirandaStatusReadyState(mockMirandaStatus);
    }

    @Test
    public void testProcessGetStatusResponseMessage () {
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");
        List<Property> properties = new ArrayList<Property>();
        List<NodeElement> cluster = new ArrayList<NodeElement>();
        StatusObject statusObject = new StatusObject(nodeElement, properties, cluster);
        GetStatusResponseMessage getStatusResponseMessage = new GetStatusResponseMessage(null, this, statusObject);

        State nextState = getReadyState().processMessage(getStatusResponseMessage);

        assert (nextState instanceof MirandaStatusReadyState);
        verify(getMockMirandaStatus()).receivedStatus(Matchers.eq(statusObject));
    }
}
