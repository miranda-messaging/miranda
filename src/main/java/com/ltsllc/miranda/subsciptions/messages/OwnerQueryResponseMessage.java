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

package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.Message;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/12/2017.
 */
public class OwnerQueryResponseMessage extends Message {
    private String owner;
    private List<String> property;
    private String sendingManager;

    public String getSendingManager() {
        return sendingManager;
    }

    public List<String> getProperty() {
        return property;
    }

    public String getOwner() {

        return owner;
    }

    public OwnerQueryResponseMessage (BlockingQueue<Message> senderQueue, Object sender, String owner,
                                      List<String> property, String sendingManager) {
        super(Subjects.OwnerQueryResponse, senderQueue, sender);

        this.owner = owner;
        this.property = property;
        this.sendingManager = sendingManager;
    }
}
