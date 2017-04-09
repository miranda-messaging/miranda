package com.ltsllc.miranda.servlet.holder;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.states.CreateUserHolderReadyState;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.user.messages.NewUserMessage;

/**
 * Created by Clark on 4/7/2017.
 */
public class CreateUserHolder extends ServletHolder {
    private static CreateUserHolder ourInstance;

    public static void initialize (long timeout) {
        CreateUserHolder createUserHolder = new CreateUserHolder(timeout);
        setInstance(createUserHolder);
    }

    public static CreateUserHolder getInstance () {
        return ourInstance;
    }

    public static void setInstance (CreateUserHolder createUserHolder) {
        ourInstance = createUserHolder;
    }

    private boolean userCreated;

    public boolean getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(boolean userCreated) {
        this.userCreated = userCreated;
    }

    public CreateUserHolder(long timeout) {
        super("create user holder", timeout);

        CreateUserHolderReadyState createUserHolderReadyState = new CreateUserHolderReadyState(this);
        setCurrentState(createUserHolderReadyState);
    }

    public boolean createUser (User user) {
        Miranda.getInstance().getUserManager().sendNewUser(getQueue(), this, user);
        setUserCreated(false);

        waitFor();
        return getUserCreated();
    }

    public void setUserCreatedAndWakeup (boolean userCreated) {
        setUserCreated(userCreated);
        wake();
    }
}
