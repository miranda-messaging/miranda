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

package com.ltsllc.miranda.session.messages;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/27/2017.
 */
public class CheckSessionResponseMessage extends Message {
    private Session session;
    private Results result;

    public Results getResult() {
        return result;
    }

    public Session getSession() {
        return session;
    }

    public CheckSessionResponseMessage(BlockingQueue<Message> senderQueue, Object sender, Results result, Session session) {
        super(Subjects.CheckSessionResponse, senderQueue, sender);

        this.result = result;
        this.session = session;
    }
}
