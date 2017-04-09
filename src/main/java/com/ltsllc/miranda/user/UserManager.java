package com.ltsllc.miranda.user;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.PublicKey;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.objects.LoginObject;
import com.ltsllc.miranda.user.messages.GetUserMessage;
import com.ltsllc.miranda.user.messages.GetUsersMessage;
import com.ltsllc.miranda.user.messages.GetUsersResponseMessage;
import com.ltsllc.miranda.user.messages.NewUserMessage;
import com.ltsllc.miranda.user.states.UserManagerReadyState;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/31/2017.
 */
public class UserManager extends Consumer {
    private static Logger logger = Logger.getLogger(UserManager.class);

    private UsersFile usersFile;
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public UsersFile getUsersFile() {
        return usersFile;
    }

    public UserManager(String filename) {
        super("users");

        UserManagerReadyState userManagerReadyState = new UserManagerReadyState(this);
        setCurrentState(userManagerReadyState);

        users = new ArrayList<User>();
        setUsers(users);

        usersFile = new UsersFile(Miranda.getInstance().getWriter(), filename);
        FileLoadedMessage fileLoadedMessage = new FileLoadedMessage(null, this);
        usersFile.addSubscriber(getQueue(), fileLoadedMessage);
        usersFile.start();
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
        garbageCollectUsers();
        getUsersFile().performGarbageCollection();
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

    public void setUsers(List<User> users) {
        this.users = users;
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
}