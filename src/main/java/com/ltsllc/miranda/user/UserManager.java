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

import com.ltsllc.clcl.DistinguishedName;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.MergeException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.objects.UserObject;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.manager.StandardManager;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.messages.UserAddedMessage;
import com.ltsllc.miranda.node.messages.UserDeletedMessage;
import com.ltsllc.miranda.node.messages.UserUpdatedMessage;
import com.ltsllc.miranda.servlet.bootstrap.BootstrapMessage;
import com.ltsllc.miranda.user.messages.*;
import com.ltsllc.miranda.user.states.UserManagerReadyState;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/31/2017.
 */
public class UserManager extends StandardManager<User> {
    public static final String NAME = "user manager";

    private static Logger logger = Logger.getLogger(UserManager.class);

    public List<User> getUsers() {
        return getData();
    }

    public UsersFile getUsersFile() {
        return (UsersFile) getFile();
    }

    public UserManager(String filename) throws IOException, MirandaException {
        super(NAME, filename);
    }

    public SingleFile<User> createFile(String filename) throws IOException, MirandaException {
        return new UsersFile(Miranda.getInstance().getReader(), Miranda.getInstance().getWriter(), filename);
    }


    public boolean contains(User user) {
        for (User aUser : getUsers())
            if (aUser.equals(user))
                return true;

        return false;
    }

    public void addUser(User user) throws DuplicateUserException {
        if (contains(user)) {
            throw new DuplicateUserException("The system already contains this user");
        } else {
            getUsers().add(user);
            getUsersFile().sendNewUserMessage(getQueue(), this, user);
        }
    }

    public void sendGetUser(BlockingQueue<Message> senderQueue, Object sender, String user) {
        GetUserMessage getUserMessage = new GetUserMessage(senderQueue, sender, user);
        sendToMe(getUserMessage);
    }

    public User getUser(String name) {
        for (User user : getUsers()) {
            if (user.getName().equals(name))
                return user;
        }

        return null;
    }

    public void deleteUser(String name) {
        User user = getUser(name);

        if (user != null) {
            getUsers().remove(user);
        }
    }

    public void setUsers(List<User> users) {
        setData(users);
    }

    public void sendGetUsers(BlockingQueue<Message> senderQueue, Object sender) {
        ListUsersMessage getUsersMessage = new ListUsersMessage(senderQueue, sender);
        sendToMe(getUsersMessage);
    }

    public void sendUpdateUserMessage(BlockingQueue<Message> senderQueue, Object sender, User user) {
        UpdateUserMessage updateUserMessage = new UpdateUserMessage(senderQueue, sender, null, user);
        sendToMe(updateUserMessage);
    }

    public void sendDeleteUserMessage(BlockingQueue<Message> senderQueue, Object sender, String name) {
        DeleteUserMessage deleteUserMessage = new DeleteUserMessage(senderQueue, sender, null, name);
        sendToMe(deleteUserMessage);
    }

    public void sendBootstrap (BlockingQueue<Message> senderQueue, Object senderObject,
                               DistinguishedName adminDistinguishedName, String adminPassword) {
        BootstrapMessage bootstrapMessage = new BootstrapMessage(senderQueue, senderObject, adminDistinguishedName,
                adminPassword);
        sendToMe(bootstrapMessage);
    }

    public void updateUser(User user) throws UnknownUserException, MergeException {
        User existingUser = getUser(user.getName());

        if (null == existingUser) {
            throw new UnknownUserException("User " + user.getName() + " was not found.");
        } else {
            existingUser.merge(user);
            getUsersFile().sendUpdateObjectsMessage(getQueue(), this, user);
            Miranda.getInstance().getCluster().sendUpdateUserMessage(getQueue(), this, existingUser);
        }
    }

    public void sendGetUserMessage(BlockingQueue<Message> senderQueue, Object sender, String name) {
        GetUserMessage getUserMessage = new GetUserMessage(senderQueue, sender, name);
        sendToMe(getUserMessage);
    }

    public void sendUserAddedMessage(BlockingQueue<Message> senderQueue, Object sender, User user) {
        UserAddedMessage userAddedMessage = new UserAddedMessage(senderQueue, sender, user);
        sendToMe(userAddedMessage);
    }

    public void sendUserUpdatedMessage(BlockingQueue<Message> senderQueue, Object sender, User user) {
        UserUpdatedMessage userUpdatedMessage = new UserUpdatedMessage(senderQueue, sender, user);
        sendToMe(userUpdatedMessage);
    }

    public void sendUserDeletedMessage(BlockingQueue<Message> senderQueue, Object sender, String name) {
        UserDeletedMessage userDeletedMessage = new UserDeletedMessage(senderQueue, sender, name);
        sendToMe(userDeletedMessage);
    }

    public void sendCreateUserMessage(BlockingQueue<Message> senderQueue, Object sender, User user) {
        CreateUserMessage createUserMessage = new CreateUserMessage(senderQueue, sender, null, user);
        sendToMe(createUserMessage);
    }

    public User convert(User user) {
        return user;
    }

    public State getReadyState () throws MirandaException {
        return new UserManagerReadyState(this);
    }
}