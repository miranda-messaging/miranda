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

package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Topic;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.clientinterface.objects.UserObject;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.mock;

/**
 * Created by Clark on 3/4/2017.
 */
public class TestMiranda extends TestCase {
    @Mock
    private Session mockSession;

    private Miranda miranda;

    public Miranda getMiranda() {
        return miranda;
    }

    public Session getMockSession() {
        return mockSession;
    }

    public void reset () throws Exception {
        super.reset();

        this.mockSession = null;
        this.miranda = null;
    }

    @Before
    public void setup () throws Exception {
        super.setup();

        String[] empty = new String[0];

        mockSession = mock(Session.class);
        miranda = new Miranda(empty);
        setupMockHttpServer();
    }

    @Test
    public void testReset () {
        setuplog4j();
        
        getMiranda().reset();

        assert (Miranda.properties == null);
        assert (Miranda.fileWatcher == null);
        assert (Miranda.timer == null);
    }

    @Test
    public void testStop () {
        getMiranda().stop();

        assert (getMiranda().getCurrentState() instanceof StopState);
    }

    @Test
    public void testSendNewSessionMessage () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();

        Session session = new Session(user,123, 456);
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        getMiranda().setQueue(queue);

        getMiranda().sendAddSessionMessage(null, this, session);

        assert (contains(Message.Subjects.AddSession, queue));
    }

    @Test
    public void testSendExpiredSessions () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "Publisher","whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();

        Session session = new Session(user,123, 456);
        List<Session> expiredSessions = new ArrayList<Session>();
        expiredSessions.add(session);
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        getMiranda().setQueue(queue);

        getMiranda().sendSessionsExpiredMessage(null, this, expiredSessions);

        assert (contains(Message.Subjects.SessionsExpired, queue));
    }

    @Test
    public void testSendDeleteTopicMessage () {
        Topic topic = new Topic("whatever");
        getMiranda().stop();
        getMiranda().sendDeleteTopicMessage(null, this, getMockSession(),"whatever");
        assert (contains(Message.Subjects.DeleteTopic, getMiranda().getQueue()));
    }

    public static final String TEST_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpjR9MH5cTEPIXR/0cLp/Lw3QDK4RMPIygL8Aqh0yQ/MOpQtXrBzwSph4N1NURg1tB3EuyCVGsTfSfrbR5nqsN5IiaJyBuvhThBLwHyKN+PEUQ/rB6qUyg+jcPigTfqj6gksNxnC6CmCJ6XpBOiBOORgFQvdISo7pOqxZKxmaTqwIDAQAB";

    @Test
    public void testSendDeleteUserMessage () {
        getMiranda().stop();
        getMiranda().sendDeleteUserMessage(null, this, getMockSession(), "whatever");
        assert (contains(Message.Subjects.DeleteUser, getMiranda().getQueue()));
    }

    @Test
    public void testSendCreateUserMessage () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "Publisher","whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        getMiranda().stop();
        getMiranda().sendCreateUserMessage(null,this, getMockSession(), user);

        assert (contains(Message.Subjects.CreateUser, getMiranda().getQueue()));
    }

    @Test
    public void testSendUpdateUserMessage () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "Publisher","whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        getMiranda().stop();
        getMiranda().sendUpdateUserMessage(null,this, getMockSession(), user);

        assert (contains(Message.Subjects.UpdateUser, getMiranda().getQueue()));
    }

    @Test
    public void testSendUserAddedMessage () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "Publisher", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        getMiranda().stop();
        getMiranda().sendUserAddedMessage(null, this, user);

        assert (contains(Message.Subjects.UserAdded, getMiranda().getQueue()));
    }

    @Test
    public void testSendUserUpdatedMessage () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "Publisher","whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        getMiranda().stop();
        getMiranda().sendUserUpdatedMessage(null, this, user);

        assert (contains(Message.Subjects.UserUpdated, getMiranda().getQueue()));
    }

    @Test
    public void testSendUserDeletedMessage () {
        getMiranda().stop();
        getMiranda().sendUserDeletedMessage(null, this, "whatever");

        assert (contains(Message.Subjects.UserDeleted, getMiranda().getQueue()));
    }
}
