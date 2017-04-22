package com.ltsllc.miranda.file.states;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.states.ClusterFileReadyState;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.file.messages.*;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.messages.GetFileMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.ImprovedRandom;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.io.File;
import java.security.SecureRandom;
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

        setuplog4j();

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

    @Test
    public void testProcessAddSubscriberMessage () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        Notification notification = new Notification(null, this);
        AddSubscriberMessage addSubscriberMessage = new AddSubscriberMessage(queue, this, notification);

        getClusterFileReadyState().processMessage(addSubscriberMessage);

        verify(getMockClusterFile(), atLeastOnce()).addSubscriber(Matchers.any(BlockingQueue.class), Matchers.eq(notification));
    }

    @Test
    public void testProcessRemoveSubscriber () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        RemoveSubscriberMessage removeSubscriberMessage = new RemoveSubscriberMessage(queue, this);

        getClusterFileReadyState().processMessage(removeSubscriberMessage);

        verify(getMockClusterFile(), atLeastOnce()).removeSubscriber(Matchers.eq(queue));
    }

    @Test
    public void testProcessAddObjectsMessage () {
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        List<NodeElement> nodeElements = new ArrayList<NodeElement>();
        NodeElement nodeElement = NodeElement.random(improvedRandom);
        nodeElements.add(nodeElement);
        nodeElement = NodeElement.random(improvedRandom);
        nodeElements.add(nodeElement);
        nodeElement = NodeElement.random(improvedRandom);
        nodeElements.add(nodeElement);
        AddObjectsMessage addObjectsMessage = new AddObjectsMessage(null, this, nodeElements);
        when(getMockClusterFile().getCurrentState()).thenReturn(getClusterFileReadyState());

        State nextState = getClusterFileReadyState().processMessage(addObjectsMessage);

        assert (nextState == getClusterFileReadyState());
        verify (getMockClusterFile(), atLeastOnce()).addObjects(Matchers.eq(nodeElements));
    }

    @Test
    public void testProcessUpdateObjectsMessage () {
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        List<NodeElement> nodeElements = new ArrayList<NodeElement>();

        for (int i = 0; i < 3; i++) {
            NodeElement nodeElement = NodeElement.random(improvedRandom);
            nodeElements.add(nodeElement);
        }

        UpdateObjectsMessage updateObjectsMessage = new UpdateObjectsMessage(null, this, nodeElements);
        when(getMockClusterFile().getCurrentState()).thenReturn(getClusterFileReadyState());

        State nextState = getClusterFileReadyState().processMessage(updateObjectsMessage);

        assert (nextState == getClusterFileReadyState());
        verify (getMockClusterFile(), atLeastOnce()).updateObjects(Matchers.eq(nodeElements));
    }

    @Test
    public void testProcessDeleteObjectsMessage () {
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        List<NodeElement> nodeElements = new ArrayList<NodeElement>();

        for (int i = 0; i < 3; i++) {
            NodeElement nodeElement = NodeElement.random(improvedRandom);
            nodeElements.add(nodeElement);
        }

        RemoveObjectsMessage removeObjectsMessage = new RemoveObjectsMessage(null, this, nodeElements);
        when(getMockClusterFile().getCurrentState()).thenReturn(getClusterFileReadyState());

        State nextState = getClusterFileReadyState().processMessage(removeObjectsMessage);

        assert (nextState == getClusterFileReadyState());
        verify (getMockClusterFile(), atLeastOnce()).removeObjects(Matchers.eq(nodeElements));
    }
}