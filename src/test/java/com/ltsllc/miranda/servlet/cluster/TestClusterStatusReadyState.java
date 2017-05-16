package com.ltsllc.miranda.servlet.cluster;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.status.GetStatusResponseMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 5/15/2017.
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

        mockClusterStatus = mock(ClusterStatus.class);
        readyState = new ClusterStatusReadyState(mockClusterStatus);
    }

    @Test
    public void testProcessGetStatusResponseMessage () {
        GetStatusResponseMessage getStatusResponseMessage = new GetStatusResponseMessage(null, this,
                null);

        State nextState = getReadyState().processMessage(getStatusResponseMessage);

        assert (nextState == getReadyState());
        verify (getMockClusterStatus(), atLeastOnce()).receivedClusterStatus(Matchers.eq(getStatusResponseMessage));
    }
}
