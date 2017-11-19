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

package com.ltsllc.miranda.user;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.server.NewObjectPostHandler;
import com.ltsllc.miranda.user.states.NewUserHandlerReadyState;

/**
 * Created by Clark on 2/10/2017.
 */
public class NewUserHandler extends NewObjectPostHandler<UsersFile> {

    public NewUserHandler(UsersFile usersFile) throws MirandaException {
        super(usersFile);

        NewUserHandlerReadyState readyState = new NewUserHandlerReadyState(this, usersFile, this);
        setCurrentState(readyState);
    }
}
