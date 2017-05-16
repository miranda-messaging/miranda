package com.ltsllc.miranda.servlet.miranda;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.servlet.property.Property;
import com.ltsllc.miranda.servlet.status.GetStatusResponseMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestMirandaStatusReadyState extends TestCase {
    @Mock
    private MirandaStatus mockMirandaStatus;

    private MirandaStatusReadyState readyState;

    public MirandaStatus getMockMirandaStatus() {
        return mockMirandaStatus;
    }

    public MirandaStatusReadyState getReadyState() {
        return readyState;
    }

    public void reset () {
        super.reset();

        mockMirandaStatus = null;
        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockMirandaStatus = mock(MirandaStatus.class);
        readyState = new MirandaStatusReadyState(mockMirandaStatus);
    }

    @Test
    public void testProcessGetStatusResponseMessage () {
        com.ltsllc.miranda.servlet.status.StatusObject statusObject = new com.ltsllc.miranda.servlet.status.StatusObject(
                null,
                new ArrayList<Property>(),
                new ArrayList<NodeElement>()

        );

        GetStatusResponseMessage getStatusResponseMessage = new GetStatusResponseMessage(null, this,
                statusObject);

        State nextState = getReadyState().processMessage(getStatusResponseMessage);

        assert (nextState == getReadyState());
        verify(getMockMirandaStatus(), atLeastOnce()).receivedStatus(Matchers.any(com.ltsllc.miranda.servlet.status.StatusObject.class));
    }
}
