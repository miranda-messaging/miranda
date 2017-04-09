package com.ltsllc.miranda.user.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.user.DuplicateUserException;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.user.UserManager;
import com.ltsllc.miranda.user.messages.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 4/1/2017.
 */
public class UserManagerReadyState extends State {
    public UserManager getUserManager () {
        return (UserManager) getContainer();
    }

    public UserManagerReadyState (UserManager userManager) {
        super(userManager);
    }

    public State processMessage (Message message) {
        State nextState = getUserManager().getCurrentState();

        switch (message.getSubject()) {
            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            case GetUser: {
                GetUserMessage getUserMessage = (GetUserMessage) message;
                nextState = processGetUserMessage(getUserMessage);
                break;
            }

            case GetUsers: {
                GetUsersMessage getUsersMessage = (GetUsersMessage) message;
                nextState = processGetUsersMessage(getUsersMessage);
                break;
            }

            case FileLoaded: {
                FileLoadedMessage fileLoadedMessage = (FileLoadedMessage) message;
                nextState = processFileLoadedMessage (fileLoadedMessage);
                break;
            }

            case NewUser: {
                NewUserMessage newUserMessage = (NewUserMessage) message;
                nextState = processNewUserMessage (newUserMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        getUserManager().performGarbageCollection();

        return getUserManager().getCurrentState();
    }

    public State processGetUserMessage (GetUserMessage getUserMessage) {
        User user = getUserManager().getUser(getUserMessage.getName());

        GetUserResponseMessage response = new GetUserResponseMessage(getUserManager().getQueue(), this, user);
        getUserMessage.reply(response);

        return getUserManager().getCurrentState();
    }

    public State processFileLoadedMessage (FileLoadedMessage fileLoadedMessage) {
        List<User> users = (List<User>) fileLoadedMessage.getData();
        List<User> newList = new ArrayList<User>(users);
        getUserManager().setUsers(newList);

        return getUserManager().getCurrentState();
    }

    public State processGetUsersMessage (GetUsersMessage getUsersMessage) {
        List<User> users = getUserManager().getUsers();

        GetUsersResponseMessage getUsersResponseMessage = new GetUsersResponseMessage(getUserManager().getQueue(),
                this, users);

        getUsersMessage.reply(getUsersResponseMessage);

        return getUserManager().getCurrentState();
    }

    public State processNewUserMessage (NewUserMessage newUserMessage) {
        Message reply = null;
        try {
            getUserManager().addUser(newUserMessage.getUser());
            reply = new UserCreatedMessage(getUserManager().getQueue(), this);
        } catch (DuplicateUserException e) {
            reply = new DuplicateUserMessage(getUserManager().getQueue(), this);
        }

        newUserMessage.reply(reply);
        return getUserManager().getCurrentState();
    }
}
