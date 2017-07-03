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
import com.ltsllc.miranda.clientinterface.Results;
import com.ltsllc.miranda.clientinterface.basicclasses.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/11/2017.
 */
public class CreateUserResponseMessage extends Message {
    private Results result;
    private User user;
    private String additionalInfo;

    public User getUser() {
        return user;
    }

    public Results getResult() {
        return result;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public CreateUserResponseMessage (BlockingQueue<Message> senderQueue, Object sender, User user, Results result) {
        super(Subjects.CreateUserResponse, senderQueue, sender);

        this.user = user;
        this.result = result;
    }

    public CreateUserResponseMessage (BlockingQueue<Message> senderQueue, Object sender, User user, Results result,
                                      String additionalInfo)
    {
        super(Subjects.CreateUserResponse, senderQueue, sender);

        this.user = user;
        this.result = result;
        this.additionalInfo = additionalInfo;
    }
}
