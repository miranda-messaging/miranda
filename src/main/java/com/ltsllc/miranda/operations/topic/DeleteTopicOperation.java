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

package com.ltsllc.miranda.operations.topic;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/23/2017.
 */
public class DeleteTopicOperation extends Operation {
    private String topicName;

    public String getTopicName() {
        return topicName;
    }

    public DeleteTopicOperation(BlockingQueue<Message> requester, Session session, String topicName) throws MirandaException {
        super("delete topic operations", requester, session);

        DeleteTopicOperationReadyState deleteTopicOperationReadyState = new DeleteTopicOperationReadyState(this);
        setCurrentState(deleteTopicOperationReadyState);

        this.topicName = topicName;
    }

    public void start() {
        super.start();

        Miranda.getInstance().getTopicManager().sendDeleteTopicMessage(getQueue(), this, getTopicName());
    }
}
