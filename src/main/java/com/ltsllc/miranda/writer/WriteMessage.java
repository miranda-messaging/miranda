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

package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
public class WriteMessage extends Message {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String s) {
        filename = s;
    }

    private byte[] buffer;

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] b) {
        buffer = b;
    }

    public WriteMessage(String filename, byte[] buffer, BlockingQueue<Message> sender, Object senderObject) {
        super(Subjects.Write, sender, senderObject);
        setBuffer(buffer);
        setFilename(filename);
    }

    public WriteMessage (BlockingQueue<Message> senderQueue, Object senderObject) {
        super(Subjects.Write, senderQueue, senderObject);
    }
}
