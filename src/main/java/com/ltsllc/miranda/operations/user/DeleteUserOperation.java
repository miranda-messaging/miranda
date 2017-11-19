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

package com.ltsllc.miranda.operations.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.subsciptions.SubscriptionManager;
import com.ltsllc.miranda.topics.TopicManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class DeleteUserOperation extends Operation {
    public static final String NAME = "delete user operation";

    private String user;
    private List<String> subsystems;

    public String getUser() {
        return user;
    }

    public List<String> getSubsystems() {
        return subsystems;
    }

    public DeleteUserOperation(BlockingQueue<Message> requester, Session session, String user) throws MirandaException {
        super(NAME, requester, session);

        DeleteUserOperationReadyState readyState = new DeleteUserOperationReadyState(this);
        setCurrentState(readyState);

        this.user = user;
        this.subsystems = new ArrayList<String>();
        this.subsystems.add(SubscriptionManager.NAME);
        this.subsystems.add(TopicManager.NAME);
    }

    public void start() {
        super.start();

        Miranda.getInstance().getTopicManager().sendOwnerQueryMessage(getQueue(), this, getUser());
        Miranda.getInstance().getSubscriptionManager().sendOwnerQueryMessage(getQueue(), this, getUser());
    }

    public void subsystemResponded(String name) {
        String subsystem = null;

        for (String s : getSubsystems()) {
            if (name.equals(s))
                subsystem = s;
        }

        if (null != subsystem)
            getSubsystems().remove(subsystem);
    }
}
