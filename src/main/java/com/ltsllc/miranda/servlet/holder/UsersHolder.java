package com.ltsllc.miranda.servlet.holder;

import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.states.UsersHolderReadyState;
import com.ltsllc.miranda.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 4/5/2017.
 */
public class UsersHolder extends ServletHolder {
    private static UsersHolder ourInstance;

    public static UsersHolder getInstance() {
        return ourInstance;
    }

    public static void setInstance(UsersHolder usersHolder) {
        ourInstance = usersHolder;
    }

    public static void initialize(long timeoutPeriod) {
        UsersHolder usersHolder = new UsersHolder(timeoutPeriod);
        setInstance(usersHolder);
    }

    private List<User> userList;

    public List<User> getUserList() {
        if (userList == null)
            userList = new ArrayList<User>();

        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public UsersHolder(long timeoutPeriod) {
        super("users servlet", timeoutPeriod);

        UsersHolderReadyState readyState = new UsersHolderReadyState(this);
        setCurrentState(readyState);

        setInstance(this);
    }

    public List<User> getUsers() {
        setUserList(null);

        Miranda.getInstance().getUserManager().sendGetUsers(getQueue(), this);

        waitFor(getTimeoutPeriod());

        List<User> users = getUserList();

        if (null == users)
            users = new ArrayList<User>();

        return users;
    }

    public void setUsersListAndAwaken(List<User> users) {
        setUserList(users);
        wake();
    }
}
