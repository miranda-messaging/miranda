package com.ltsllc.miranda.user;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.User;
import com.ltsllc.miranda.file.SingleFileReadyState;
import com.ltsllc.miranda.miranda.SynchronizeMessage;

/**
 * Created by Clark on 2/10/2017.
 */
public class UsersFileReadyState extends SingleFileReadyState<User> {
    private UsersFile usersFile;

    public UsersFileReadyState (Consumer consumer, UsersFile usersFile) {
        super(consumer);

        this.usersFile = usersFile;
    }

    public UsersFile getUsersFile() {
        return usersFile;
    }


    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case NewUser: {
                NewUserMessage newUserMessage = (NewUserMessage) message;
                nextState = processNewUserMessage(newUserMessage);
                break;
            }


            case Synchronize: {
                SynchronizeMessage synchronizeMessage = (SynchronizeMessage) message;
                nextState = processSynchronizeMessage (synchronizeMessage);
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


    public State processSynchronizeMessage (SynchronizeMessage synchronizeMessage) {
        GetUsersFileMessage getUsersFileMessage = new GetUsersFileMessage(getUsersFile().getQueue(), this);
        send(synchronizeMessage.getNode().getQueue(), getUsersFileMessage);

        return this;
    }
}
