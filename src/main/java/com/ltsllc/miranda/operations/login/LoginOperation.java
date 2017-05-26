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

package com.ltsllc.miranda.operations.login;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.operations.Operation;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class LoginOperation extends Operation {
    public static final String NAME = "login operation";

    private String user;

    public String getUser() {
        return user;
    }

    public LoginOperation (String name, BlockingQueue<Message> requester) {
        super (NAME, requester);

        this.user = name;

        LoginOperationReadyState readyState = new LoginOperationReadyState(this);
        setCurrentState(readyState);
    }

    public void start () {
        super.start();

        Miranda.getInstance().getSessionManager().sendGetSessionMessage(getQueue(), this, getUser());
    }
}
