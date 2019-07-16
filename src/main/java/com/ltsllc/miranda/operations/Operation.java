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

package com.ltsllc.miranda.operations;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.session.Session;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class Operation extends Consumer {
    protected Session session;
    private BlockingQueue<Message> requester;
    private UUID uuid;

    public String getUUIDString () {
        if (null == uuid) {
            uuid = UUID.randomUUID();
        }

        return uuid.toString();
    }

    public BlockingQueue<Message> getRequester() {
        return requester;
    }

    public Operation(String name, BlockingQueue<Message> requester, Session session) throws MirandaException {
        super(name);

        this.requester = requester;
        this.session = session;
    }

    public Operation(String name, BlockingQueue<Message> requester) {
        super(name);
        this.requester = requester;
    }

    public Session getSession() {
        return session;
    }
}
