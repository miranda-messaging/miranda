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
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.messages.GetFileResponseMessage;
import com.ltsllc.miranda.file.states.SingleFileReadyState;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.messages.GetFileMessage;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.messages.VersionMessage;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.user.UsersFile;
import com.ltsllc.miranda.user.messages.NewUserMessage;
import com.ltsllc.miranda.util.Utils;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/10/2017.
 */
public class UsersFileReadyState extends SingleFileReadyState {
    private static Logger logger = Logger.getLogger(UsersFileReadyState.class);

    public UsersFileReadyState (UsersFile usersFile) {
        super(usersFile);
    }

    public UsersFile getUsersFile() {
        return (UsersFile) getContainer();
    }


    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case GetVersion: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) message;
                nextState = processGetVersionMessage(getVersionMessage);
                break;
            }

            case NewUser: {
                NewUserMessage newUserMessage = (NewUserMessage) message;
                nextState = processNewUserMessage(newUserMessage);
                break;
            }

            case GetFile: {
                GetFileMessage getFileMessage = (GetFileMessage) message;
                nextState = processGetFileMessage (getFileMessage);
                break;
            }

            default:
                super.processMessage(message);
        }

        return nextState;
    }


    private State processNewUserMessage (NewUserMessage newUserMessage) {
        getUsersFile().addUser(newUserMessage.getUser());

        return this;
    }


    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        NameVersion nameVersion = new NameVersion("users", getUsersFile().getVersion());
        VersionMessage versionMessage = new VersionMessage(getUsersFile().getQueue(), this, nameVersion);
        send(getVersionMessage.getRequester(), versionMessage);

        return this;
    }


    public Message getFileMessage () {
        GetFileMessage getFileMessage = new GetFileMessage(getUsersFile().getQueue(), this, "users");
        return getFileMessage;
    }


    public State getSyncingState() {
        return new UsersFileSyncingState(getUsersFile());
    }


    @Override
    public Version getVersion() {
        return getUsersFile().getVersion();
    }

    private State processGetFileMessage (GetFileMessage getFileMessage) {
        String hexString = Utils.bytesToString(getUsersFile().getBytes());
        GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(getUsersFile().getQueue(), this, "users", hexString);
        send(getFileMessage.getSender(), getFileResponseMessage);

        return this;
    }

    public void write() {
        byte[] buffer = getUsersFile().getBytes();
        WriteMessage writeMessage = new WriteMessage(getUsersFile().getFilename(), buffer, getUsersFile().getQueue(), this);
        send(getUsersFile().getWriterQueue(), writeMessage);
    }


    public Type getListType() {
        return new TypeToken<List<User>> () {}.getType();
    }


    @Override
    public void add(Object o) {
        User user = (User) o;
        getUsersFile().getData().add(user);
    }

    @Override
    public boolean contains(Object o) {
        User user = (User) o;
        for (User u : getUsersFile().getData()) {
            if (u.equals(user))
                return true;
        }

        return false;
    }

    @Override
    public SingleFile getFile() {
        return getUsersFile();
    }


    @Override
    public String getName() {
        return "users";
    }


    @Override
    public List<Perishable> getPerishables() {
        return new ArrayList<Perishable>(getUsersFile().getData());
    }
}
