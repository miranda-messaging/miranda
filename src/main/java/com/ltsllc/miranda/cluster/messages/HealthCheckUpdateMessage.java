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

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/10/2017.
 */
public class HealthCheckUpdateMessage extends Message {
    private List<NodeElement> updates;

    public HealthCheckUpdateMessage(BlockingQueue<Message> senderQueue, Object sender, List<NodeElement> updates) {
        super(Subjects.HealthCheckUpdate, senderQueue, sender);

        this.updates = updates;
    }

    public List<NodeElement> getUpdates() {
        return updates;
    }
}
