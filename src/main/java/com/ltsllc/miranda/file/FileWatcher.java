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

package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.file.messages.FileChangedMessage;
import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/25/2017.
 */
public class FileWatcher {
    private static Logger logger = Logger.getLogger(FileWatcher.class);

    private BlockingQueue<Message> queue;

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    public FileWatcher (BlockingQueue<Message> queue) {
        this.queue = queue;
    }

    public void sendMessage (String filename) {
        try {
            File file = new File(filename);
            FileChangedMessage fileChangedMessage = new FileChangedMessage(getQueue(), this, file);
            getQueue().put(fileChangedMessage);
        } catch (InterruptedException e) {
            Panic panic = new Panic("Exception while trying to send meaasge", e, Panic.Reasons.ExceptionSendingMessage);
            Miranda.getInstance().panic(panic);
        }
    }
}
