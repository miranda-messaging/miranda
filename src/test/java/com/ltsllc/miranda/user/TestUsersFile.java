package com.ltsllc.miranda.user;

import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.states.UsersFileReadyState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 4/3/2017.
 */
public class TestUsersFile extends TestCase {
    public static final String TEST_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMOinA1ha2eTP/9KwszAhYfbNJiapjz8/3mgTnglRxi7Hi1cJSTODks7SKzzkDdM+GsQctOTMYMA3hittfuU3PiCv0hmDotwpdjvW+5r2xJ+DuFV7dSZOEVMeMJlO2MJEPFS0KPI/DUdy8+A//yu4qPzzC5A6U1zJ1jcQNzl/WUwIDAQAB";

    private UsersFile usersFile;

    public UsersFile getUsersFile() {
        return usersFile;
    }

    public void reset () {
        super.reset();

        usersFile = null;
    }

    public static String TEST_FILENAME = "testfile";

    public static final String[] TEST_FILE_CONTENTS = {
            "[ ",
            "{",
            "    \"name\" : \"what\",",
            "    \"description\" : \"an expired user\", ",
            "    \"status\" : \"New\",",
            "    \"publicKey\" : \"" + TEST_KEY + "\"",
            "}",
            "]"
    };

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        createFile(TEST_FILENAME, TEST_FILE_CONTENTS);
        usersFile = new UsersFile(getMockWriter(), TEST_FILENAME);
    }

    @After
    public void cleanup () {
        deleteFile(TEST_FILENAME);
    }

    @Test
    public void testConstructor () {
        assert (getUsersFile().getFilename().equals(TEST_FILENAME));
        assert (getUsersFile().getWriter() == getMockWriter());
        assert (getUsersFile().getCurrentState() instanceof UsersFileReadyState);
        assert (UsersFile.getInstance() == getUsersFile());
    }

    @Test
    public void testAddUser () {
        getUsersFile().start();


        User newUser = null;
        try {
            newUser = new User("what", "ever", TEST_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getUsersFile().addUser(newUser);

        pause(100);

        verify(getMockWriter(), atLeastOnce()).sendWrite(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.anyString(),
                Matchers.any(byte[].class));
    }

    @Test
    public void testRemoveUsers () {
        List<User> users = new ArrayList<User>();
        User newUser = null;
        try {
            newUser = new User("what", "ever", TEST_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        users.add(newUser);

        getUsersFile().addUser(newUser);
        getUsersFile().removeUsers(users);

        verify(getMockWriter(), atLeast(2)).sendWrite(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.anyString(), Matchers.any(byte[].class));
        assert (!getUsersFile().contains(newUser));
    }

    public static final String[] TEST_LOAD_CONTENTS = {
            "[",
            "{",
            "    \"name\" : \"what\", ",
            "    \"description\" : \"ever\", ",
            "    \"publicKey\" : \"" + TEST_KEY + "\"",
            "}",
            "]",
    };

    @Test
    public void testLoad () {
        createFile(TEST_FILENAME, TEST_LOAD_CONTENTS);

        getUsersFile().load();

        User user = getUsersFile().getData().get(0);

        assert (null != user);
        assert (user.getPublicKey() != null);
    }
}
