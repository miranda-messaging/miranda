package com.ltsllc.miranda.servlet.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.cluster.ClusterStatusReadyState;
import com.ltsllc.miranda.servlet.cluster.ClusterStatus;
import com.ltsllc.miranda.servlet.messages.GetStatusResponseMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 3/24/2017.
 */
public class TestClusterStatusReadyState extends TestCase {
    @Mock
    private ClusterStatus mockClusterStatus;

    private ClusterStatusReadyState readyState;

    public ClusterStatusReadyState getReadyState() {
        return readyState;
    }

    public ClusterStatus getMockClusterStatus() {
        return mockClusterStatus;
    }

    public void reset () {
        super.reset();

        mockClusterStatus = null;
        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        this.mockClusterStatus = mock(ClusterStatus.class);
        readyState = new ClusterStatusReadyState(mockClusterStatus);
    }

    @Test
    public void testProcessGetStatusResponseMessage () {
        GetStatusResponseMessage getStatusResponnseMessage = new GetStatusResponseMessage(null, this, "whatever");

        State nextState = getReadyState().processMessage(getStatusResponnseMessage);

        assert (nextState instanceof ClusterStatusReadyState);
        verify(getMockClusterStatus(), atLeastOnce()).receivedClusterStatus(Matchers.any(GetStatusResponseMessage.class));
    }
}
