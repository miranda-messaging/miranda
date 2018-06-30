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

package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.message.Message;

import java.io.File;
import java.util.concurrent.BlockingQueue;

import static com.ltsllc.miranda.message.Message.Subjects.WatchFile;

/**
 * Created by Clark on 2/25/2017.
 */
public class WatchFileMessage extends Message {
    private File file;
    private BlockingQueue<Message> listener;

    public File getFile() {
        return file;
    }

    public BlockingQueue<Message> getListener() {
        return listener;
    }

    public WatchFileMessage(BlockingQueue<Message> senderQueue, Object sender, File file, BlockingQueue<Message> listener) {
        super(WatchFile, senderQueue, sender);

        this.file = file;
        this.listener = listener;
    }
}
