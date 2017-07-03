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

package com.ltsllc.miranda.operations.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.session.Session;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class CreateUserOperation extends Operation {
    public static final String NAME = "create user operation";


    private User user;

    public User getUser() {
        return user;
    }

    public CreateUserOperation (BlockingQueue<Message> requester, Session session, User user) {
        super( NAME, requester, session);

        CreateUserOperationReadyState readyState = new CreateUserOperationReadyState( this);
        setCurrentState(readyState);

        this.user = user;
    }

    public void start () {
        super.start();

        Miranda.getInstance().getUserManager().sendCreateUserMessage(getQueue(), this, getUser());
    }

}
