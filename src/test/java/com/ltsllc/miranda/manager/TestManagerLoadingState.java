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

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;
import com.ltsllc.miranda.cluster.states.ClusterLoadingState;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 5/14/2017.
 */
public class TestManagerLoadingState extends TestCase {
    private ManagerLoadingState loadingState;

    public ManagerLoadingState getLoadingState() {
        return loadingState;
    }

    public void reset () throws Exception {
        super.reset();

        loadingState = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        setuplog4j();

        loadingState = new ClusterLoadingState(getMockCluster());
    }

    @Test
    public void testProcessFileLoadedMessage () throws MirandaException {
        List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
        NodeElement nodeElement = new NodeElement("fooo.com", 6789, "a node");
        nodeElementList.add(nodeElement);

        FileLoadedMessage fileLoadedMessage = new FileLoadedMessage(null, this, nodeElementList);

        State nextState = getLoadingState().processMessage(fileLoadedMessage);

        assert (nextState instanceof ManagerReadyState);
    }
}
