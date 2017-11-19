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

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.messages.FileChangedMessage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/25/2017.
 */
public abstract class FileWatcher implements Watcher {
    abstract boolean scan() throws IOException;

    private static Logger logger = Logger.getLogger(FileWatcher.class);

    private File file;
    private BlockingQueue<Message> listener;

    public BlockingQueue<Message> getListener() {
        return listener;
    }

    public File getFile() {
        return file;
    }

    public FileWatcher(File file, BlockingQueue<Message> listener) {
        this.file = file;
        this.listener = listener;
    }

    public void check() throws IOException {
        boolean changed = scan();

        if (changed)
            notifyListener();
    }

    public void notifyListener() {
        FileChangedMessage fileChangedMessage = new FileChangedMessage(null, this, getFile());
        Consumer.staticSend(fileChangedMessage, getListener());
    }
}
