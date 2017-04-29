package com.ltsllc.miranda.user;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Manager;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.messages.UserAddedMessage;
import com.ltsllc.miranda.node.messages.UserDeletedMessage;
import com.ltsllc.miranda.node.messages.UserUpdatedMessage;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.user.messages.*;
import com.ltsllc.miranda.user.states.UserManagerReadyState;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/31/2017.
 */
public class UserManager extends Manager<User, User> {
    private static Logger logger = Logger.getLogger(UserManager.class);

    public List<User> getUsers() {
        return getData();
    }

    public UsersFile getUsersFile() {
        return (UsersFile) getFile();
    }

    public UserManager(String filename) {
        super("users", new UsersFile(Miranda.getInstance().getWriter(), filename));

        UserManagerReadyState userManagerReadyState = new UserManagerReadyState(this);
        setCurrentState(userManagerReadyState);
    }

    public void start () {
        super.start();

        FileLoadedMessage fileLoadedMessage = new FileLoadedMessage(null, this);
        getUsersFile().sendAddSubscriberMessage(getQueue(), this, fileLoadedMessage);
    }

    public void garbageCollectUsers () {
        List<User> expired = new ArrayList<User>();

        for (User user : getUsers()) {
            if (user.expired()) {
                logger.info ("The user, " + user.getName() + " has expired.  Deleting.");
                expired.add(user);
            }
        }

        getUsers().removeAll(expired);
        getUsersFile().removeUsers(expired);
    }

    public void performGarbageCollection () {
        super.performGarbageCollection();

        garbageCollectUsers();
    }

    public boolean contains (User user) {
        for (User aUser : getUsers())
            if (aUser.equals(user))
                return true;

        return false;
    }

    public void addUser (User user) throws DuplicateUserException {
        if (contains(user)) {
            throw new DuplicateUserException("The system already contain this user");
        } else {
            getUsers().add (user);
            getUsersFile().sendNewUserMessage(getQueue(), this, user);
        }
    }

    public void sendGetUser (BlockingQueue<Message> senderQueue, Object sender, String user) {
        GetUserMessage getUserMessage = new GetUserMessage(senderQueue, sender, user);
        sendToMe(getUserMessage);
    }

    public User getUser (String name) {
        for (User user : getUsers()) {
            if (user.getName().equals(name))
                return user;
        }

        return null;
    }

    public void deleteUser (String name) {
        User user = getUser(name);

        if (user != null) {
            getUsers().remove(user);
        }
    }

    public void setUsers(List<User> users) {
        setData(users);
    }

    public void sendGetUsers (BlockingQueue<Message> senderQueue, Object sender) {
        GetUsersMessage getUsersMessage = new GetUsersMessage(senderQueue, sender);
        sendToMe(getUsersMessage);
    }

    public void sendNewUser (BlockingQueue<Message> senderQueue, Object sender, User user)
    {
        NewUserMessage newUserMessage = new NewUserMessage(senderQueue, sender, user);
        sendToMe(newUserMessage);
    }

    public void sendUpdateUserMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        UpdateUserMessage updateUserMessage = new UpdateUserMessage (senderQueue, sender, null, user);
        sendToMe(updateUserMessage);
    }

    public void sendDeleteUserMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        DeleteUserMessage deleteUserMessage = new DeleteUserMessage (senderQueue, sender, null, name);
        sendToMe(deleteUserMessage);
    }

    public void updateUser (UserObject userObject) throws MirandaException {
        User existingUser = getUser(userObject.getName());

        if (null == existingUser)
            throw new UnknownUserException ("User " + userObject.getName() + " not found");

        existingUser.updateFrom (userObject);
    }

    public void updateUser (User user) throws UnknownUserException {
        User existingUser = getUser(user.getName());

        if (null == existingUser) {
            throw new UnknownUserException("User " + user.getName() + " was not found.");
        } else {
            existingUser.updateFrom(user);
        }
    }

    public void sendGetUserMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        GetUserMessage getUserMessage = new GetUserMessage(senderQueue, sender, name);
        sendToMe(getUserMessage);
    }

    public void sendUserAddedMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        UserAddedMessage userAddedMessage = new UserAddedMessage(senderQueue, sender, user);
        sendToMe(userAddedMessage);
    }

    public void sendUserUpdatedMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        UserUpdatedMessage userUpdatedMessage = new UserUpdatedMessage(senderQueue, sender, user);
        sendToMe(userUpdatedMessage);
    }

    public void sendUserDeletedMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        UserDeletedMessage userDeletedMessage = new UserDeletedMessage(senderQueue, sender, name);
        sendToMe(userDeletedMessage);
    }

    public void sendCreateUserMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        CreateUserMessage createUserMessage = new CreateUserMessage(senderQueue, sender, null, user);
        sendToMe(createUserMessage);
    }

    public void sendGarbageCollectionMessage (BlockingQueue<Message> senderQueue, Object sender) {
        GarbageCollectionMessage garbageCollectionMessage = new GarbageCollectionMessage(senderQueue, sender);
        sendToMe(garbageCollectionMessage);
    }

    public User convert (User user) {
        return user;
    }
}