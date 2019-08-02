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

package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 5/14/2017.
 */
public class TestManager extends TestCase {
    private Manager manager;

    public Manager getManager() {
        return manager;
    }

    public void reset () throws Exception {
        super.reset();

        manager = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        setuplog4j();
        setupMiranda();
        manager = new Cluster(getMockNetwork(), true);
    }

    @Test
    public void testConstructor () {
        assert (getManager().getName().equals(Cluster.NAME));
        assert (getManager().getData() != null);
        assert (getManager().getData().size() < 1);
    }

    @Test
    public void testConvertList () throws MirandaException {
        List<NodeElement> temp = new ArrayList<NodeElement>();
        NodeElement nodeElement = new NodeElement("foo.com", 6789, "a node");
        temp.add(nodeElement);
        nodeElement = new NodeElement("bar.com", 6789, "another node");
        temp.add(nodeElement);

        List<Node> nodes = getManager().convertList(temp);
    }

    @Test
    public void testConvert () throws MirandaException {
        NodeElement nodeElement = new NodeElement("foo.com", 6789, "a node");
        Node node = (Node) getManager().convert(nodeElement);

        assert (node.getDns().equals("foo.com"));
        assert (node.getPort() == 6789);
        assert (node.getDescription().equals("a node"));
    }

    @Test
    public void testPerformGarbageCollection () {
        setupMockSingleFile();
        getManager().setFile(getMockSingleFile());
        getManager().performGarbageCollection();

        verify(getMockSingleFile(), atLeastOnce()).sendGarbageCollectionMessage(Matchers.any(BlockingQueue.class),
                Matchers.any());
    }
}
