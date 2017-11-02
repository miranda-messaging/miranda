/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.user;

import com.ltsllc.common.util.Utils;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.objects.StatusObject;
import com.ltsllc.miranda.file.Subscriber;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.states.UserManagerStartState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 4/2/2017.
 */
public class TestUserManager extends TestCase {
    public static final String TEST_PUBLIC_KEY_PEM = "-----BEGIN PUBLIC KEY-----\n"
            + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1avWB4w2AtIN/DOSyyDu\n"
            + "dN7OA3XVbjyq9cKkVkLtHuKQYvq2w1sFoToeZ15R+J7WxDGFuSzdWa/RbR5LLNeM\n"
            + "BqgGZ+/jwGOipRtUMVa8467ZV5BL6vowkjAyUUevTABUxzTo+YvwrL8LPVpNOO1v\n"
            + "VmAsWOe+lTyeQkAILaSeCvyjdmDRr5O5U5UILlAcZDJ8LFOm9kNQQ4yIVUqAMbBo\n"
            + "MF+vPrmEA09tMqrmR5lb4RsmAUlDxiMWCU9AxwWfksHbd7fV8puvnxjuI1+TZ7SS\n"
            + "Fk1L/bPothhCjsWYr4RMVDluzSAgqsFbAgLXGpraDibVOOrmmBtG2ngu9NJV5fGA\n"
            + "NwIDAQAB\n"
            + "-----END PUBLIC KEY-----\n";

    private UserManager userManager;

    public UserManager getUserManager() {
        return userManager;
    }

    public void reset() {
        super.reset();

        userManager = null;
    }

    public static final String TEST_FILENAME = "testfile";
    public static final String[] TEST_FILE_CONTENTS = {
            "[",
            "{",
            "    \"name\" : \"what\",",
            "    \"category\" : \"Publisher\",",
            "    \"description\" : \"a test user\",",
            "    \"publicKeyPem\" : \"" + TEST_PUBLIC_KEY_PEM + "\"",
            "}",
            "]"
    };

    @Before
    public void setup() {
        reset();

        super.setup();

        setuplog4j();

        setupMiranda();
        setupMockReader();
        setupMockWriter();
        setupMockUsersFile();

        createFile(TEST_FILENAME, TEST_FILE_CONTENTS);

        try {
            userManager = new UserManager(TEST_FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanup() {
        deleteFile(TEST_FILENAME);
    }

    public void testConstructor_original() {
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
    public void testConstructor() {
        assert (getUserManager().getCurrentState() instanceof UserManagerStartState);
        assert (getUserManager().getUsers() != null);
        assert (getUserManager().getUsersFile() != null);
    }

    public boolean isSubscriber(BlockingQueue<Message> queue, UsersFile usersFile) {
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
            "    \"category\" : \"Publisher\",",
            "    \"status\" : \"Deleted\", ",
            "    \"publicKey\" : \"" + TEST_PUBLIC_KEY_PEM + "\"",
            "}",
            "]"
    };


    @Test
    public void testContains() throws Exception {
        User shouldContain = new User("whatever", "whatever");
        getUserManager().addUser(shouldContain);
        User shouldNotContain = new User("not here", "absent");

        assert (getUserManager().contains(shouldContain));
        assert (!getUserManager().contains(shouldNotContain));
    }

    @Test
    public void testAddUser() throws DuplicateUserException {
        getUserManager().setFile(getMockUsersFile());
        User newUser = null;

        newUser = new User("what", "Publisher", "ever", TEST_PUBLIC_KEY_PEM);

        assert (!getUserManager().contains(newUser));

        getUserManager().addUser(newUser);

        assert (getUserManager().contains(newUser));
        verify(getMockUsersFile(), atLeastOnce()).sendNewUserMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.any(User.class));
    }

    @Test
    public void testGetUser() throws DuplicateUserException {
        getUserManager().setFile(getMockUsersFile());

        User newUser = new User("what", "ever");

        setupMockReader();
        setupMockWriter();

        getUserManager().contains(newUser);

        getUserManager().addUser(newUser);

        User user = getUserManager().getUser("what");

        assert (null != user);
        assert (user.equals(newUser));
    }
}
