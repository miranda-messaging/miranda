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

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.states.SingleFileSyncingState;
import com.ltsllc.miranda.user.UsersFile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/11/2017.
 */
public class UsersFileSyncingState extends SingleFileSyncingState {
    private UsersFile usersFile;

    public UsersFileSyncingState(UsersFile usersFile) throws MirandaException {
        super(usersFile);
        this.usersFile = usersFile;
    }

    public UsersFile getUsersFile() {
        return usersFile;
    }


    public Type getListType() {
        return new TypeToken<ArrayList<User>>() {
        }.getType();
    }


    public State getReadyState() throws MirandaException {
        return new UsersFileReadyState(getUsersFile());
    }


    public boolean contains(Object o) {
        User otherUser = (User) o;

        for (User user : getUsersFile().getData()) {
            if (user.equals(otherUser))
                return true;
        }

        return false;
    }


    @Override
    public List getData() {
        return getUsersFile().getData();
    }


    @Override
    public String getName() {
        return "users";
    }


    @Override
    public SingleFile getFile() {
        return getUsersFile();
    }
}
