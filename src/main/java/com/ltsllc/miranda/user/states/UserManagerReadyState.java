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

import com.ltsllc.clcl.*;
import com.ltsllc.commons.io.Util;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.MergeException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.manager.StandardManagerReadyState;
import com.ltsllc.miranda.manager.states.ManagerReadyState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.node.messages.UserAddedMessage;
import com.ltsllc.miranda.node.messages.UserDeletedMessage;
import com.ltsllc.miranda.node.messages.UserUpdatedMessage;
import com.ltsllc.miranda.servlet.bootstrap.BootstrapMessage;
import com.ltsllc.miranda.user.DuplicateUserException;
import com.ltsllc.miranda.user.UnknownUserException;
import com.ltsllc.miranda.user.UserManager;
import com.ltsllc.miranda.user.messages.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.ltsllc.miranda.Results.UsersExist;

/**
 * Created by Clark on 4/1/2017.
 */
public class UserManagerReadyState extends ManagerReadyState {
    private Logger logger = Logger.getLogger(UserManagerReadyState.class);

    public UserManager getUserManager() {
        return (UserManager) getContainer();
    }

    public UserManagerReadyState(UserManager userManager) throws MirandaException {
        super(userManager);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getUserManager().getCurrentState();

        switch (message.getSubject()) {
            case GetUser: {
                GetUserMessage getUserMessage = (GetUserMessage) message;
                nextState = processGetUserMessage(getUserMessage);
                break;
            }

            case ListUsers: {
                ListUsersMessage getUsersMessage = (ListUsersMessage) message;
                nextState = processGetUsersMessage(getUsersMessage);
                break;
            }

            case DeleteUser: {
                DeleteUserMessage deleteUserMessage = (DeleteUserMessage) message;
                nextState = processDeleteUserMessage(deleteUserMessage);
                break;
            }

            case CreateUser: {
                CreateUserMessage createUserMessage = (CreateUserMessage) message;
                nextState = processCreateUserMessage(createUserMessage);
                break;
            }

            case UpdateUser: {
                UpdateUserMessage updateUserMessage = (UpdateUserMessage) message;
                nextState = processUpdateUserMessage(updateUserMessage);
                break;
            }

            case UserAdded: {
                UserAddedMessage userAddedMessage = (UserAddedMessage) message;
                nextState = processUserAddedMessage(userAddedMessage);
                break;
            }

            case UserUpdated: {
                UserUpdatedMessage userUpdatedMessage = (UserUpdatedMessage) message;
                nextState = processUserUpdatedMessage(userUpdatedMessage);
                break;
            }

            case UserDeleted: {
                UserDeletedMessage userDeletedMessage = (UserDeletedMessage) message;
                nextState = processUserDeletedMessage(userDeletedMessage);
                break;
            }

            case Bootstrap: {
                BootstrapMessage bootstrapMessage = (BootstrapMessage) message;
                nextState = processBootstrapMessage (bootstrapMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processGarbageCollectionMessage(GarbageCollectionMessage garbageCollectionMessage) {
        getUserManager().performGarbageCollection();

        return getUserManager().getCurrentState();
    }

    public State processGetUserMessage(GetUserMessage getUserMessage) throws MirandaException {
        User user = getUserManager().getUser(getUserMessage.getName());

        Results result = user == null ? Results.UserNotFound : Results.Success;

        GetUserResponseMessage response = new GetUserResponseMessage(getUserManager().getQueue(), this,
                getUserMessage.getName(), result, user);

        getUserMessage.reply(response);

        return getUserManager().getCurrentState();
    }

    public State processFileLoadedMessage(FileLoadedMessage fileLoadedMessage) {
        List<User> users = (List<User>) fileLoadedMessage.getData();
        List<User> newList = new ArrayList<User>(users);
        getUserManager().setUsers(newList);

        return getUserManager().getCurrentState();
    }

    public State processGetUsersMessage(ListUsersMessage getUsersMessage) throws MirandaException {
        List<User> users = getUserManager().getUsers();

        GetUsersResponseMessage getUsersResponseMessage = new GetUsersResponseMessage(getUserManager().getQueue(),
                this, users);

        getUsersMessage.reply(getUsersResponseMessage);

        return getUserManager().getCurrentState();
    }

    public State processCreateUserMessage(CreateUserMessage createUserMessage) throws MirandaException {
        CreateUserResponseMessage reply = null;
        try {
            getUserManager().addUser(createUserMessage.getUser());
            reply = new CreateUserResponseMessage(getUserManager().getQueue(), this, createUserMessage.getUser(),
                    Results.Success);
        } catch (DuplicateUserException e) {
            reply = new CreateUserResponseMessage(getUserManager().getQueue(), this, createUserMessage.getUser(),
                    Results.Duplicate);
        }

        createUserMessage.reply(reply);

        if (reply.getResult() == Results.Success) {
            List<User> userList = new ArrayList<User>();
            userList.add(createUserMessage.getUser());

            getUserManager().getUsersFile().sendAddObjectsMessage(getUserManager().getQueue(), this, userList);
        }

        return getUserManager().getCurrentState();
    }

    public State processUpdateUserMessage(UpdateUserMessage updateUserMessage) throws MirandaException {
        Results result = Results.Unknown;

        try {
            getUserManager().updateUser(updateUserMessage.getUser());
            result = Results.Success;
        } catch (MirandaException e) {
            result = Results.Exception;
        }

        User user = getUserManager().getUser(updateUserMessage.getUser().getName());
        UpdateUserResponseMessage userUpdateResponseMessage = new UpdateUserResponseMessage(getUserManager().getQueue(),
                this, user, result);

        updateUserMessage.reply(userUpdateResponseMessage);

        if (userUpdateResponseMessage.getResult() == Results.Success) {
            getUserManager().getUsersFile().sendUpdateObjectsMessage(getUserManager().getQueue(), this, user);
        }

        return getUserManager().getCurrentState();
    }

    public State processDeleteUserMessage(DeleteUserMessage deleteUserMessage) throws MirandaException {
        User existingUser = getUserManager().getUser(deleteUserMessage.getName());
        DeleteUserResponseMessage deleteUserResponseMessage = new DeleteUserResponseMessage(getUserManager().getQueue(),
                this, deleteUserMessage.getName());

        if (null == existingUser) {
            deleteUserResponseMessage.setResult(Results.UserNotFound);
        } else {
            getUserManager().deleteUser(deleteUserMessage.getName());
            deleteUserResponseMessage.setResult(Results.Success);
        }

        deleteUserMessage.reply(deleteUserResponseMessage);

        if (deleteUserResponseMessage.getResult() == Results.Success) {
            getUserManager().getUsersFile().sendRemoveObjectsMessage(getUserManager().getQueue(), this,
                    existingUser);
        }

        return getUserManager().getCurrentState();
    }

    public State processUserAddedMessage(UserAddedMessage userAddedMessage) {
        logger.info("Adding user " + userAddedMessage.getUser().getName());
        try {
            getUserManager().addUser(userAddedMessage.getUser());
        } catch (DuplicateUserException e) {
            logger.error("Duplicate user " + userAddedMessage.getUser().getName() + " will continue");
        }

        getUserManager().getUsersFile().sendAddObjectsMessage(getUserManager().getQueue(), this,
                userAddedMessage.getUser());

        return getUserManager().getCurrentState();
    }

    public State processUserUpdatedMessage(UserUpdatedMessage userUpdatedMessage) {
        logger.info("Updating user " + userUpdatedMessage.getUser().getName());

        try {
            getUserManager().updateUser(userUpdatedMessage.getUser());
        } catch (UnknownUserException e) {
            logger.error("Asked to update unknown user " + userUpdatedMessage.getUser().getName());
        } catch (MergeException e) {
            logger.error("Exception while merging", e);
        }

        getUserManager().getUsersFile().sendUpdateObjectsMessage(getUserManager().getQueue(), this,
                userUpdatedMessage.getUser());

        return getUserManager().getCurrentState();
    }

    public State processUserDeletedMessage(UserDeletedMessage userDeletedMessage) {
        User existingUser = getUserManager().getUser(userDeletedMessage.getName());
        logger.info("Deleting user " + userDeletedMessage.getName());

        getUserManager().deleteUser(userDeletedMessage.getName());

        if (existingUser != null) {
            getUserManager().getUsersFile().sendRemoveObjectsMessage(getUserManager().getQueue(), this,
                    existingUser);
        }

        return getUserManager().getCurrentState();
    }

    public State processBootstrapMessage (BootstrapMessage bootstrapMessage) throws MirandaException {
        try {
            if (getUserManager().getUsers().size() > 1) {
                BootstrapResponseMessage bootstrapResponseMessage = new BootstrapResponseMessage(getUserManager().getQueue(),
                        this, com.ltsllc.miranda.Results.UsersExist);
            } else {
                KeyPair keyPair = KeyPair.newKeys();
                keyPair.getPublicKey().setDn(bootstrapMessage.getAdminDistinguishedName());
                String pem = keyPair.getPublicKey().toPem();
                Util.writeTextFile("admin.public.pem", pem);
                keyPair.getPrivateKey().setDn(bootstrapMessage.getAdminDistinguishedName());
                pem = keyPair.getPrivateKey().toPem();
                Util.writeTextFile("admin.private.pem", pem);
                JavaKeyStore javaKeyStore = new JavaKeyStore();
                javaKeyStore.setFilename("admin.jks");
                javaKeyStore.setPasswordString(bootstrapMessage.getAdminPassword());
                javaKeyStore.add("admin", keyPair, null);
                javaKeyStore.store();
                User admin = new User("admin", User.UserTypes.Admin, "The admin user", keyPair.getPublicKey());
                getUserManager().addUser(admin);
            }
        } catch (EncryptionException|IOException e) {
            MirandaException mirandaException = new MirandaException("Exception creating keys", e);
            throw mirandaException;
        } catch (DuplicateUserException e) {
            MirandaException mirandaException = new MirandaException("There is already an admin user", e);
            throw mirandaException;
        }

        return this;
    }
}
