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

package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;
import com.ltsllc.miranda.cluster.ClusterFile;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/20/2017.
 */

/**
 * The {@link ClusterFile} discovered a new node.
 */
public class NewNodeElementMessage extends Message {
    private NodeElement node;

    public NewNodeElementMessage(BlockingQueue<Message> senderQueue, Object sender, NodeElement newNode) {
        super(Subjects.NewNodeElement, senderQueue, sender);

        this.node = newNode;
    }

    public NodeElement getNode() {
        return node;
    }
}
