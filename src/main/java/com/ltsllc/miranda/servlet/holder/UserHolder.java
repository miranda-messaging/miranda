package com.ltsllc.miranda.servlet.holder;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.servlet.states.UserHolderReadyState;
import com.ltsllc.miranda.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/10/2017.
 */
public class UserHolder extends ServletHolder {
    private static UserHolder ourInstance;

    private Results userCreateResults;
    private Results userUpdateResults;
    private Results userDeleteResults;
    private Results getUserResults;
    private List<User> userList;
    private User user;

    public Results getGetUserResults() {
        return getUserResults;
    }

    public void setGetUserResults(Results getUserResults) {
        this.getUserResults = getUserResults;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public Results getUserCreateResults() {
        return userCreateResults;
    }

    public void setUserCreateResults(Results userCreateResults) {
        this.userCreateResults = userCreateResults;
    }

    public Results getUserDeleteResults() {
        return userDeleteResults;
    }

    public void setUserDeleteResults(Results userDeleteResults) {
        this.userDeleteResults = userDeleteResults;
    }

    public Results getUserUpdateResults() {
        return userUpdateResults;
    }

    public void setUserUpdateResults(Results userUpdateResults) {
        this.userUpdateResults = userUpdateResults;
    }

    public static UserHolder getInstance () {
        return ourInstance;
    }

    public static void setInstance (UserHolder userHolder) {
        ourInstance = userHolder;
    }

    public static void initialize (long timeout) {
        UserHolder userHolder = new UserHolder(timeout);
        setInstance(userHolder);
    }

    public UserHolder (long timeout) {
        super("user holder", timeout);

        UserHolderReadyState readyState = new UserHolderReadyState(this);
        setCurrentState(readyState);
    }

    public List<User> getUsers () throws TimeoutException {
        Miranda.getInstance().getUserManager().sendGetUsers(getQueue(), this);

        sleep();

        return getUserList();
    }

    public void setUsersAndAwaken (List<User> users) {
        List<User> userList = new ArrayList<User>(users);
        setUserList(userList);
        wake();
    }

    public User getUser (String name) throws TimeoutException {
        setUser(null);
        Miranda.getInstance().getUserManager().sendGetUserMessage(getQueue(), this, name);

        sleep();

        return getUser();
    }

    public Results createUser (User user) throws TimeoutException {
        setUserCreateResults(Results.Unknown);
        Miranda.getInstance().sendCreateUserMessage(getQueue(), this, user);

        sleep();

        return getUserCreateResults();
    }

    public void setUserCreatedAndAwaken (Results result) {
        setUserCreateResults(result);
        wake();
    }

    public void setUserAndAwaken (User user) {
        setUser(user);
        wake();
    }

    public Results updateUser (User user) throws TimeoutException {
        setUserUpdateResults(Results.Unknown);
        Miranda.getInstance().sendUpdateUserMessage (getQueue(), this, user);

        sleep();

        return getUserUpdateResults();
    }

    public void setUserUpdatedAndAwaken (Results result) {
        setUserUpdateResults(result);
        wake();
    }

    public Results deleteUser (String name) throws TimeoutException {
        setUserDeleteResults(Results.Unknown);
        Miranda.getInstance().sendDeleteUserMessage (getQueue(), this, name);

        sleep();

        return getUserDeleteResults();
    }

    public void setUserDeletedAndAwaken (Results result) {
        setUserDeleteResults(result);
        wake();
    }

}
