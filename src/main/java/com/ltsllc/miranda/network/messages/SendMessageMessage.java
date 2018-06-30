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

package com.ltsllc.miranda.network.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/1/2017.
 */
public class SendMessageMessage extends Message {
    private int handle;
    private byte[] content;

    public int getHandle() {
        return handle;
    }

    public byte[] getContent() {
        return content;
    }

    public SendMessageMessage(BlockingQueue<Message> queue, Object sender, int handle, byte[] content) {
        super(Subjects.SendMessage, queue, sender);

        this.handle = handle;
        this.content = content;
    }


    public SendMessageMessage(BlockingQueue<Message> queue, Object sender, int handle, String content) {
        super(Subjects.SendMessage, queue, sender);

        this.handle = handle;
        this.content = content.getBytes();
    }
}
