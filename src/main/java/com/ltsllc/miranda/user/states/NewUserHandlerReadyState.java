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

package com.ltsllc.miranda.user.states;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.server.NewObjectHandlerReadyState;
import com.ltsllc.miranda.user.NewUserHandler;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.user.UsersFile;

import java.lang.reflect.Type;

/**
 * Created by Clark on 2/18/2017.
 */
public class NewUserHandlerReadyState extends NewObjectHandlerReadyState<UsersFile, User, NewUserHandler> {
    @Override
    public Type getBasicType() {
        return User.class;
    }

    public NewUserHandlerReadyState (Consumer consumer, UsersFile usersFile, NewUserHandler newUserHandler) {
        super(consumer, usersFile, newUserHandler);
    }

    public State processMessage (Message message) {
        return super.processMessage(message);
    }
}
