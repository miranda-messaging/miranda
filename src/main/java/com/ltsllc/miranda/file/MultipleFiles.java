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
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/10/2017.
 */
abstract public class MultipleFiles {
    private static Logger logger = Logger.getLogger(MultipleFiles.class);

    private String directoryName;
    private BlockingQueue<Message> writerQueue;

    public MultipleFiles(String directoryName, BlockingQueue<Message> writerQueue) {
        this.writerQueue = writerQueue;
        setDirectoryName(directoryName);
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String s) {
        directoryName = s;
    }

    public BlockingQueue<Message> getWriterQueue() {
        return writerQueue;
    }

    public void fileChanged(String s) {
        logger.info(s + " changed");
    }


}
