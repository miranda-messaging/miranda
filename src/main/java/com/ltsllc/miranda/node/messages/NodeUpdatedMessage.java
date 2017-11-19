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

package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/8/2017.
 */

/**
 * Indicates that the sender has changed.
 */
public class NodeUpdatedMessage extends Message {
    private NodeElement oldNode;
    private NodeElement newNode;

    public NodeUpdatedMessage(BlockingQueue<Message> senderQueue, Object sender, NodeElement oldNode, NodeElement newNode) {
        super(Subjects.NodeUpdated, senderQueue, sender);

        this.oldNode = oldNode;
        this.newNode = newNode;
    }

    public NodeElement getNewNode() {
        return newNode;
    }

    public NodeElement getOldNode() {

        return oldNode;
    }
}
