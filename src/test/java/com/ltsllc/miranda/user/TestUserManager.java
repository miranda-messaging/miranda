package com.ltsllc.miranda.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.StatusObject;
import com.ltsllc.miranda.file.Subscriber;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.states.UserManagerReadyState;
import com.ltsllc.miranda.util.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.when;

/**
 * Created by Clark on 4/2/2017.
 */
public class TestUserManager extends TestCase {
    public static final String TEST_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMOinA1ha2eTP/9KwszAhYfbNJiapjz8/3mgTnglRxi7Hi1cJSTODks7SKzzkDdM+GsQctOTMYMA3hittfuU3PiCv0hmDotwpdjvW+5r2xJ+DuFV7dSZOEVMeMJlO2MJEPFS0KPI/DUdy8+A//yu4qPzzC5A6U1zJ1jcQNzl/WUwIDAQAB";

    private UserManager userManager;

    public UserManager getUserManager() {
        return userManager;
    }

    public void reset () {
        super.reset();

        userManager = null;
    }

    public static final String TEST_FILENAME = "testfile";
    public static final String[] TEST_FILE_CONTENTS = {
            "[",
            "{",
            "    \"name\" : \"what\",",
            "    \"description\" : \"a test user\",",
            "    \"publicKey\" : \"" + TEST_KEY + "\"",
            "}",
            "]"
    };

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        setupMockMiranda();

        when (getMockMiranda().getWriter()).thenReturn(getMockWriter());
        createFile(TEST_FILENAME, TEST_FILE_CONTENTS);

        userManager = new UserManager(TEST_FILENAME);
    }

    @After
    public void cleanup () {
        deleteFile(TEST_FILENAME);
    }

    public void testConstructor_original () {
        try {
            Key key = Utils.loadKey("root.keystore", "whatever", "root");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(key);
            objectOutputStream.close();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            String hexString = Utils.bytesToString(bytes);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConstructor () {
        assert (getUserManager().getCurrentState() instanceof UserManagerReadyState);
        assert (getUserManager().getUsers() != null);
        assert (getUserManager().getUsersFile() != null);
    }

    public boolean isSubscriber (BlockingQueue<Message> queue, UsersFile usersFile) {
        for (Subscriber subscriber : usersFile.getSubscribers()) {
            if (subscriber.getQueue() == queue)
                return true;
        }

        return false;
    }

    @Test
    public void testStart() {
        getUserManager().start();

        pause(50);

        assert (isSubscriber(getUserManager().getQueue(), getUserManager().getUsersFile()));
    }

    public static final String[] EXPIRED_CONTENTS = {
            "[ ",
            "{",
            "    \"name\" : \"what\",",
            "    \"description\" : \"an expired user\", ",
            "    \"status\" : \"Deleted\", ",
            "    \"publicKey\" : \"" + TEST_KEY + "\"",
            "}",
            "]"
    };

    @Test
    public void testPerformGarbageCollection () {
        getUserManager().start();

        setupMockMiranda();

        when(getMockMiranda().getWriter()).thenReturn(getMockWriter());

        createFile(TEST_FILENAME, EXPIRED_CONTENTS);

        this.userManager = new UserManager(TEST_FILENAME);
        this.userManager.start();

        pause(50);

        assert(getUserManager().getUsers().size() > 0);

        getUserManager().performGarbageCollection();

        assert (getUserManager().getUsers().size() < 1);
    }

    @Test
    public void testContains () {
        User shouldContain = new User ("what", "an expired user");
        shouldContain.setStatus(StatusObject.Status.Deleted);
        User shouldNotContain = new User ("not here", "absent");

        getUserManager().start();

        pause(50);

        assert (getUserManager().contains(shouldContain));
        assert (!getUserManager().contains(shouldNotContain));
    }

    @Test
    public void testAddUser () {
        User newUser = null;

        try {
            newUser = new User("what", "ever", TEST_KEY);

            assert (!getUserManager().contains(newUser));

            getUserManager().addUser(newUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert (getUserManager().contains(newUser));
    }

    @Test
    public void testGetUser () {
        User newUser = new User("what", "ever");

        getUserManager().contains(newUser);

        try {
            getUserManager().addUser(newUser);
        } catch (DuplicateUserException e) {
            e.printStackTrace();
        }

        User user = getUserManager().getUser("what");

        assert (null != user);
        assert (user.equals(newUser));
    }
}
