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

package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.requests.Files;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/11/2017.
 */
public class GetFileMessage extends Message {
    private Files file;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }

    public GetFileMessage(BlockingQueue<Message> senderQueue, Object sender, Files file) {
        super(Subjects.GetFile, senderQueue, sender);

        setFile(file);
    }


}
