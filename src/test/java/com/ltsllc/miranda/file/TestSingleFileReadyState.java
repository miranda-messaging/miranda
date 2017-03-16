package com.ltsllc.miranda.file;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.ClusterFileReadyState;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.messages.GetFileMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/15/2017.
 */
public class TestSingleFileReadyState extends TestCase {
    @Mock
    private ClusterFile mockClusterFile;

    private ClusterFileReadyState clusterFileReadyState;

    public void reset () {
        super.reset();

        this.mockClusterFile = null;
        this.clusterFileReadyState = null;
    }

    public ClusterFileReadyState getClusterFileReadyState() {
        return clusterFileReadyState;
    }

    public ClusterFile getMockClusterFile() {

        return mockClusterFile;
    }

    @Before
    public void setup () {
        super.setup();

        this.mockClusterFile = mock(ClusterFile.class);
        this.clusterFileReadyState = new ClusterFileReadyState(mockClusterFile);
    }

    @Test
    public void testProcessLoadMessage () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        LoadMessage loadMessage = new LoadMessage(queue, this);
        List<NodeElement> emptyList = new ArrayList<NodeElement>();

        when(getMockClusterFile().getData()).thenReturn(emptyList);

        getClusterFileReadyState().processMessage(loadMessage);

        verify(getMockClusterFile(), atLeastOnce()).load();
        assert (contains(Message.Subjects.LoadResponse, queue));
    }

    /**
     * I'm not certain this ever gets used.
     */
    @Test
    public void testGetFileResponseMessage () {

    }

    private byte[] FILE_BYTES = {91, 93};

    @Test
    public void testGetFileMessage () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        Gson gson = new Gson();
        String json = gson.toJson(queue);
        byte[] buffer = json.getBytes();
        GetFileMessage getFileMessage = new GetFileMessage(queue, this, "whatever");

        when(getMockClusterFile().getQueue()).thenReturn(queue);
        when(getMockClusterFile().getBytes()).thenReturn(FILE_BYTES);
        when(getMockClusterFile().getFilename()).thenReturn("whatever");

        getClusterFileReadyState().processMessage(getFileMessage);

        assert (contains(Message.Subjects.GetFileResponse, queue));
    }

    @Test
    public void testProcessGarbageCollectionMessage () {
        GarbageCollectionMessage garbageCollectionMessage = new GarbageCollectionMessage(null, this);
        getClusterFileReadyState().processMessage(garbageCollectionMessage);

        verify(getMockClusterFile(), atLeastOnce()).performGarbageCollection();
    }

}

