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

package com.ltsllc.miranda.timer.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/22/2017.
 */
public class ScheduleMessage extends Message {
    private Message message;
    private BlockingQueue<Message> receiver;

    public ScheduleMessage(Subjects subject, BlockingQueue<Message> senderQueue, Object sender,
                           BlockingQueue<Message> receiver, Message message) {
        super(subject, senderQueue, sender);

        this.message = message;
        this.receiver = receiver;
    }


    public Message getMessage() {
        return message;
    }

    public BlockingQueue<Message> getReceiver() {
        return receiver;
    }
}
