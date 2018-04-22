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

package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.test.TestCase;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;

public class TesterNodeState extends TestCase {
    @Mock
    private Node mockNode;

    public Node getMockNode() {
        return mockNode;
    }

    public void reset () throws Exception {
        super.reset();

        mockNode = null;
    }

    public void setup () throws Exception {
        super.setup();

        setuplog4j();

        mockNode = mock(Node.class);
    }
}
