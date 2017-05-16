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

package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/2/2017.
 */
public class GetUserResponseMessage extends Message {
    private String name;
    private User user;
    private Results result;

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public Results getResult() {
        return result;
    }

    public GetUserResponseMessage (BlockingQueue<Message> senderQueue, Object sender, String name, Results result, User user) {
        super(Subjects.GetUserResponse, senderQueue, sender);

        this.result = result;
        this.name = name;
        this.user = user;
    }
}
