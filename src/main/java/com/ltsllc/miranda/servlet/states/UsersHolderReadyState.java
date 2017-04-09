package com.ltsllc.miranda.servlet.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.holder.UsersHolder;
import com.ltsllc.miranda.user.messages.GetUsersResponseMessage;

/**
 * Created by Clark on 4/5/2017.
 */
public class UsersHolderReadyState extends State {
    public UsersHolder getUsersHolder () {
        return (UsersHolder) getContainer();
    }

    public UsersHolderReadyState (UsersHolder usersHolder) {
        super(usersHolder);
    }

    public State processMessage (Message message) {
        State nextState = getUsersHolder().getCurrentState();

        switch (message.getSubject()) {
            case GetUsersResponse: {
                GetUsersResponseMessage response = (GetUsersResponseMessage) message;
                nextState = processGetUsersResponseMessage(response);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processGetUsersResponseMessage (GetUsersResponseMessage getUsersResponseMessage) {
        getUsersHolder().setUsersListAndAwaken (getUsersResponseMessage.getUsers());

        return getUsersHolder().getCurrentState();
    }
}
