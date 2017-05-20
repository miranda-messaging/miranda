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

import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.states.UsersFileReadyState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 4/3/2017.
 */
public class TestUsersFile extends TestCase {
    public static final String TEST_PUBLIC_KEY_PEM = "-----BEGIN PUBLIC KEY-----\n"
            + "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1avWB4w2AtIN/DOSyyDu\n"
            + "dN7OA3XVbjyq9cKkVkLtHuKQYvq2w1sFoToeZ15R+J7WxDGFuSzdWa/RbR5LLNeM\n"
            + "BqgGZ+/jwGOipRtUMVa8467ZV5BL6vowkjAyUUevTABUxzTo+YvwrL8LPVpNOO1v\n"
            + "VmAsWOe+lTyeQkAILaSeCvyjdmDRr5O5U5UILlAcZDJ8LFOm9kNQQ4yIVUqAMbBo\n"
            + "MF+vPrmEA09tMqrmR5lb4RsmAUlDxiMWCU9AxwWfksHbd7fV8puvnxjuI1+TZ7SS\n"
            + "Fk1L/bPothhCjsWYr4RMVDluzSAgqsFbAgLXGpraDibVOOrmmBtG2ngu9NJV5fGA\n"
            + "NwIDAQAB\n"
            + "-----END PUBLIC KEY-----";


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
            "    \"category\" : \"Publisher\", ",
            "    \"status\" : \"New\",",
            "    \"publicKeyPem\" : \"" + TEST_PUBLIC_KEY_PEM + "\"",
            "}",
            "]"
    };

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        createFile(TEST_FILENAME, TEST_FILE_CONTENTS);
        usersFile = new UsersFile(getMockReader(), getMockWriter(), TEST_FILENAME);
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
            newUser = new User("what", "Publisher","ever", TEST_PUBLIC_KEY_PEM);
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
            newUser = new User("what", "Publisher", "ever", TEST_PUBLIC_KEY_PEM);
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
            "    \"status\" : \"New\",",
            "    \"name\" : \"what\", ",
            "    \"description\" : \"ever\", ",
            "    \"category\" : \"Publisher\", ",
            "    \"publicKeyPem\" : \"" + TEST_PUBLIC_KEY_PEM + "\"",
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
