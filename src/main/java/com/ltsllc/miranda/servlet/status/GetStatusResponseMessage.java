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

package com.ltsllc.miranda.servlet.status;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/9/2017.
 */
public class GetStatusResponseMessage extends Message {
    private Object statusObject;

    public Object getStatusObject() {
        return statusObject;
    }

    public GetStatusResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Object statusObject) {
        super(Subjects.GetStatusResponse, senderQueue, sender);

        this.statusObject = statusObject;
    }
}
