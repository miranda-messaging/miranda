package com.ltsllc.miranda.user.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.servlet.objects.LoginObject;
import com.ltsllc.miranda.session.LoginResponseMessage;
import com.ltsllc.miranda.user.UnknownUserException;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.user.UserManager;
import com.ltsllc.miranda.user.messages.GetUserMessage;
import com.ltsllc.miranda.user.messages.GetUserResponseMessage;
import com.ltsllc.miranda.user.messages.LoginMessage;

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

            case FileLoaded: {
                FileLoadedMessage fileLoadedMessage = (FileLoadedMessage) message;
                nextState = processFileLoadedMessage (fileLoadedMessage);
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
        getUserManager().setUsers(users);

        return getUserManager().getCurrentState();
    }
}
