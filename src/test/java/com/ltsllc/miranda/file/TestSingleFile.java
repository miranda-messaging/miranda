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

package com.ltsllc.miranda.file;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.test.NodeElementFileCreator;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.ImprovedRandom;
import com.ltsllc.miranda.util.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;


/**
 * Created by Clark on 3/14/2017.
 */
public class TestSingleFile extends TestCase {
    public static final String TEST_DIRECTORY = "testdir";
    public static final String TEST_FILE = TEST_DIRECTORY + "/" + "tesfile";

    public static final String[][] FILE_SYSTEM_SPEC = {
        { "test_cluster_file.json", "nodeElementFile"}
    };

    private SingleFile<NodeElement> singleFile;
    private BlockingQueue<Message> queue;

    public SingleFile<NodeElement> getSingleFile() {
        return singleFile;
    }

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    public void reset () {
        super.reset();

        this.queue = null;
        this.singleFile = null;
    }

    @Before
    public void setup () {
        super.setup();

        setuplog4j();
        setupMirandaProperties();
        setupDirectory();
        setupRandomFile();

        this.singleFile = new ClusterFile(TEST_FILE, getMockReader(), getMockWriter(), getQueue());
    }

    @After
    public void cleanup () {
        deleteDirectory(TEST_DIRECTORY);
    }


    public void setupDirectory () {
        File dir = new File(TEST_DIRECTORY);
        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) {
                System.err.println("Could not create directory: " + dir.getName());
                System.exit(1);
            }
        }
    }

    public void setupRandomFile () {
        File testFile = new File(TEST_FILE);
        ImprovedRandom random = new ImprovedRandom(new SecureRandom());
        NodeElementFileCreator nodeElementFileCreator = new NodeElementFileCreator(random, 16);
        if (!nodeElementFileCreator.createFile(testFile)) {
            System.err.println("Could not create test file: " + testFile.getName());
            System.exit(1);
        }
    }

    public void setupFile (String filename, List<NodeElement> nodeElementList) {
        Gson gson = new Gson();
        String json = gson.toJson(nodeElementList);

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filename);
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Exception setting up file: " + filename);
        } finally {
            Utils.closeIgnoreExceptions(fileWriter);
        }
    }

    private static final String TEST_FILE_CONTENTS = "5B0A20207B0A2020202022646E73223A2022666F6F2E636F6D222C0A20202020226970223A20223139322E3136382E312E31222C0A2020202022706F7274223A20363738392C0A20202020226465736372697074696F6E223A202261206E6F6465222C0A20202020226C617374436F6E6E6563746564223A202D310A20207D0A5D";
    private static final String TEST_FILENAME2 = TEST_DIRECTORY + "/test2";

    @Test
    public void testLoad () throws IOException {
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        nodeElementList.add(nodeElement);

        setupFile(TEST_FILE, nodeElementList);
        this.singleFile = new ClusterFile(TEST_FILE, getMockReader(), getMockWriter(), getQueue());

        getSingleFile().load();

        byte[] data = Utils.hexStringToBytes(TEST_FILE_CONTENTS);
        setupDirectory();
        makeFile(TEST_FILENAME2, data);

        ClusterFile clusterFile = new ClusterFile(TEST_FILENAME2, getMockReader(), getMockWriter(), getQueue());
        clusterFile.load();

        assert (getSingleFile().equals(clusterFile));
    }

    @Test
    public void testGetBytes () throws IOException {
        NodeElement nodeElement = new NodeElement("foo.com", "192.168.1.1", 6789, "a node");
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        nodeElementList.add(nodeElement);

        this.singleFile = new ClusterFile(TEST_FILE, getMockReader(), getMockWriter(), getQueue());
        getSingleFile().setData(nodeElementList);

        byte[] data = getSingleFile().getBytes();
        String s = Utils.bytesToString(data);

        String s2 = Utils.hexStringToString(s);
        assert (s.equals(TEST_FILE_CONTENTS));
    }

    @Test
    public void testContains () {
        ImprovedRandom random = new ImprovedRandom(new SecureRandom());
        NodeElement nodeElement = NodeElement.random(random);
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        nodeElementList.add(nodeElement);

        getSingleFile().setData(nodeElementList);

        NodeElement notPresent = new NodeElement("test.com", "192.168.1.2", 6789, "a node that could not be generated radomly");

        assert (getSingleFile().contains(nodeElement));
        assert (!getSingleFile().contains(notPresent));
    }

    @Test
    public void testEquals () {
        ImprovedRandom random = new ImprovedRandom(new SecureRandom());
        NodeElement nodeElement = NodeElement.random(random);
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();

        setupFile(TEST_FILE, nodeElementList);

        NodeElement notPresent = new NodeElement("test.com", "192.168.1.2", 6789, "a node that could not be generated radomly");
        List<NodeElement> nodeElementList2 = new ArrayList<NodeElement>();
        nodeElementList2.add(notPresent);

        ClusterFile clusterFile2 = new ClusterFile(TEST_FILE, getMockReader(), getMockWriter(), getQueue(), nodeElementList2);

        assert (getSingleFile().equals(getSingleFile()));
        assert (!getSingleFile().equals(nodeElementList2));
    }

    public boolean contains (List<Subscriber> subscribers, BlockingQueue<Message> queue) {
        for (Subscriber subscriber : subscribers) {
            if (subscriber.getQueue() == queue)
                return true;
        }

        return false;
    }

    @Test
    public void testAddSubscriber () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        getSingleFile().addSubscriber(queue);

        assert (contains(getSingleFile().getSubscribers(), queue));
    }

    @Test
    public void testRemoveSubscriber () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        getSingleFile().removeSubscriber(queue);

        assert (!contains(getSingleFile().getSubscribers(), queue));
    }

    public static final String TEST_FILENAME3 = "testfile";

    public static final String[] TEST_FILE_CONTENTS3 = {
            "hello, world!"
    };


    @Test
    public void testReadFileSuccess () {
        createFile(TEST_FILENAME3, TEST_FILE_CONTENTS3);

        String contents = SingleFile.readFile(TEST_FILENAME3);

        assert (contents.equals("hello, world!\r\n"));
    }

    @Test
    public void testReadFileIOException () {
        setupMockMiranda();

        String contents = SingleFile.readFile("wrong");

        assert (null == contents);
        verify(getMockMiranda(), atLeastOnce()).panic(Matchers.any(Panic.class));
    }

    @Test
    public void testAddObjects () {
        setupMockWriter();
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        List<NodeElement> nodeElements = new ArrayList<NodeElement>();
        for (int i = 0; i < 3; i++ ) {
            NodeElement nodeElement = NodeElement.random(improvedRandom);
            nodeElements.add(nodeElement);
        }

        getSingleFile().addObjects(nodeElements);

        assert (getSingleFile().contains(nodeElements.get(0)));
        assert (getSingleFile().contains(nodeElements.get(1)));
        assert (getSingleFile().contains(nodeElements.get(2)));
        verify (getMockWriter(), atLeastOnce()).sendWrite(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyString(), Matchers.any(byte[].class));
    }

    @Test
    public void testUpdateObjects () {
        setupMockWriter();
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        List<NodeElement> nodeElements = new ArrayList<NodeElement>();
        for (int i = 0; i < 3; i++ ) {
            NodeElement nodeElement = NodeElement.random(improvedRandom);
            nodeElements.add(nodeElement);
        }

        getSingleFile().addObjects(nodeElements);
        List<NodeElement> updates = new ArrayList<NodeElement>();

        for (int i = 0; i < 3; i++) {
            NodeElement nodeElement = nodeElements.get(i);
            String dns = nodeElement.getDns();
            int port = nodeElement.getPort();
            nodeElement = NodeElement.random(improvedRandom);
            nodeElement.setDns(dns);
            nodeElement.setPort(port);
            updates.add(nodeElement);
        }

        getSingleFile().updateObjects(updates);

        assert (getSingleFile().contains(updates.get(0)));
        assert (getSingleFile().contains(updates.get(1)));
        assert (getSingleFile().contains(updates.get(2)));
        verify (getMockWriter(), atLeast(2)).sendWrite(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyString(), Matchers.any(byte[].class));
    }

    @Test
    public void testRemoveObjects () {
        setupMockWriter();
        SecureRandom secureRandom = new SecureRandom();
        ImprovedRandom improvedRandom = new ImprovedRandom(secureRandom);
        List<NodeElement> nodeElements = new ArrayList<NodeElement>();
        for (int i = 0; i < 3; i++ ) {
            NodeElement nodeElement = NodeElement.random(improvedRandom);
            nodeElements.add(nodeElement);
        }

        getSingleFile().addObjects(nodeElements);
        getSingleFile().removeObjects(nodeElements);

        assert (getSingleFile().getData().size() < 1);
        verify (getMockWriter(), atLeast(2)).sendWrite(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.anyString(), Matchers.any(byte[].class));
    }
}
