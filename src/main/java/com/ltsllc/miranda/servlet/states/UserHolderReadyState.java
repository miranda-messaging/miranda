package com.ltsllc.miranda.servlet.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.holder.UserHolder;
import com.ltsllc.miranda.session.messages.CheckSessionResponseMessage;
import com.ltsllc.miranda.user.messages.*;

/**
 * Created by Clark on 4/5/2017.
 */
public class UserHolderReadyState extends ServletHolderReadyState {
    public UserHolder getUserHolder () {
        return (UserHolder) getContainer();
    }

    public UserHolderReadyState(UserHolder userHolder) {
        super(userHolder);
    }

    public State processMessage (Message message) {
        State nextState = getUserHolder().getCurrentState();

        switch (message.getSubject()) {
            case GetUsersResponse: {
                GetUsersResponseMessage response = (GetUsersResponseMessage) message;
                nextState = processGetUsersResponseMessage(response);
                break;
            }

            case GetUserResponse: {
                GetUserResponseMessage response = (GetUserResponseMessage) message;
                nextState = processGetUserResponseMessage(response);
                break;
            }

            case CreateUserResponse: {
                CreateUserResponseMessage response = (CreateUserResponseMessage) message;
                nextState = processCreateUserResponseMessage (response);
                break;
            }

            case UpdateUserResponse: {
                UpdateUserResponseMessage response = (UpdateUserResponseMessage) message;
                nextState = processUpdateUserResponseMessage (response);
                break;
            }

            case DeleteUserResponse: {
                DeleteUserResponseMessage response = (DeleteUserResponseMessage) message;
                nextState = processDeleteUserResponseMessage (response);
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
        getUserHolder().setUsersAndAwaken (getUsersResponseMessage.getUsers());

        return getUserHolder().getCurrentState();
    }

    public State processGetUserResponseMessage (GetUserResponseMessage getUserResponseMessage) {
        getUserHolder().setGetUserResults(getUserResponseMessage.getResult());
        getUserHolder().setUserAndAwaken(getUserResponseMessage.getUser());

        return getUserHolder().getCurrentState();
    }

    public State processCreateUserResponseMessage (CreateUserResponseMessage createUserResponseMessage) {
        getUserHolder().setUserCreatedAndAwaken(createUserResponseMessage.getResult());

        return getUserHolder().getCurrentState();
    }

    public State processUpdateUserResponseMessage (UpdateUserResponseMessage updateUserResponseMessage) {
        getUserHolder().setUserUpdatedAndAwaken(updateUserResponseMessage.getResult());

        return getUserHolder().getCurrentState();
    }

    public State processDeleteUserResponseMessage (DeleteUserResponseMessage deleteUserResponseMessage) {
        getUserHolder().setUserDeletedAndAwaken(deleteUserResponseMessage.getResult());

        return getUserHolder().getCurrentState();
    }
}
