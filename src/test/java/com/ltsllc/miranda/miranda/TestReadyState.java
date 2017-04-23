package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.messages.NewConnectionMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.messages.UserAddedMessage;
import com.ltsllc.miranda.node.messages.UserDeletedMessage;
import com.ltsllc.miranda.node.messages.UserUpdatedMessage;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.session.AddSessionMessage;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.SessionManager;
import com.ltsllc.miranda.session.SessionsExpiredMessage;
import com.ltsllc.miranda.subsciptions.messages.OwnerQueryResponseMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.user.messages.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/5/2017.
 */
public class TestReadyState extends TestCase {
    private ReadyState readyState;

    @Mock
    private SessionManager mockSessionManager;

    public ReadyState getReadyState() {
        return readyState;
    }

    public SessionManager getMockSessionManager() {
        return mockSessionManager;
    }

    public void reset () {
        this.readyState = null;
    }


    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        this.readyState = new ReadyState(getMockMiranda());
        this.mockSessionManager = mock(SessionManager.class);
    }

    @Test
    public void testProcessNewConnectionMessage () {
        Node node = new Node(-1, getMockNetwork(), getMockCluster());
        NewConnectionMessage message = new NewConnectionMessage(null, this, node);

        State nextState = getReadyState().processMessage(message);

        assert (nextState instanceof ReadyState);
        assert (contains(Message.Subjects.GetVersion, node.getQueue()));
    }

    @Test
    public void testProcessAddSessionMessage () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();

        Session session = new Session(user,123, 456);
        AddSessionMessage addSessionMessage = new AddSessionMessage(null, this, session);

        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());
        when(getMockMiranda().getSessionManager()).thenReturn(getMockSessionManager());

        State nextState = getReadyState().processMessage(addSessionMessage);

        assert (nextState instanceof ReadyState);
        verify(getMockSessionManager(), atLeastOnce()).sendAddSessionMessage(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.eq(session));
    }

    @Test
    public void testProcessSessionsExpired () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();

        Session session = new Session(user,123, 456);
        List<Session> expiredSessions = new ArrayList<Session>();
        expiredSessions.add(session);

        when(getMockMiranda().getSessionManager()).thenReturn(getMockSessionManager());
        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());

        SessionsExpiredMessage sessionsExpiredMessage = new SessionsExpiredMessage(null, this, expiredSessions);

        State nextState = getReadyState().processMessage(sessionsExpiredMessage);

        assert (nextState instanceof ReadyState);
        verify (getMockSessionManager(), atLeastOnce()).sendSessionsExpiredMessage(Matchers.any(BlockingQueue.class), Matchers.any(), Matchers.eq(expiredSessions));
    }

    public Message getMessage (Message.Subjects subject, BlockingQueue<Message> queue) {
        for (Message message : queue) {
            if (subject == message.getSubject())
                return message;
        }

        return null;
    }

    @Test
    public void testProcessOwnerQueryResponseMessageUserOwnsTopics () {
        BlockingQueue<Message> requester = new LinkedBlockingQueue<Message>();
        List<String> emptyList = new ArrayList<String>();
        List<String> subsystems = new ArrayList<String>();
        subsystems.add("TopicManager");
        subsystems.add("SubscriptionManager");

        List<String> property = new ArrayList<String>();
        property.add("whatever");

        getReadyState().getDeleteUserToQueue().put("whatever", requester);
        getReadyState().getDeleteUserToSubsystems().put("whatever", subsystems);

        OwnerQueryResponseMessage ownerQueryResponseMessage = new OwnerQueryResponseMessage(null, this,
                "whatever", property, "TopicManager");

        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(ownerQueryResponseMessage);

        assert (nextState == getReadyState());

        DeleteUserResponseMessage deleteUserResponseMessage = (DeleteUserResponseMessage) getMessage(
                Message.Subjects.DeleteUserResponse, requester);

        assert (deleteUserResponseMessage.getResult() != Results.Success);
    }

    @Test
    public void testProcessOwnerQueryResponseMessageUserOwnsSubscriptions () {
        BlockingQueue<Message> requester = new LinkedBlockingQueue<Message>();
        List<String> subsystems = new ArrayList<String>();
        subsystems.add("TopicManager");
        subsystems.add("SubscriptionManager");

        List<String> property = new ArrayList<String>();
        property.add("whatever");

        getReadyState().getDeleteUserToQueue().put("whatever", requester);
        getReadyState().getDeleteUserToSubsystems().put("whatever", subsystems);

        OwnerQueryResponseMessage ownerQueryResponseMessage = new OwnerQueryResponseMessage(null, this,
                "whatever", property, "SubscriptionManager");

        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(ownerQueryResponseMessage);

        assert (nextState == getReadyState());

        DeleteUserResponseMessage deleteUserResponseMessage = (DeleteUserResponseMessage) getMessage(
                Message.Subjects.DeleteUserResponse, requester);

        assert (deleteUserResponseMessage.getResult() != Results.Success);
    }

    @Test
    public void testProcessOwnerQueryResponseMessageIntermediate ()
    {
        List<String> subsystems = new ArrayList<String>();
        subsystems.add("TopicManager");
        subsystems.add("SubscriptionManager");


        BlockingQueue<Message> requester = new LinkedBlockingQueue<Message>();
        getReadyState().getDeleteUserToQueue().put("whatever", requester);
        getReadyState().getDeleteUserToSubsystems().put("whatever", subsystems);

        List<String> emptyList = new ArrayList<String>();

        OwnerQueryResponseMessage ownerQueryResponseMessage = new OwnerQueryResponseMessage(null, this,
                "whatever", emptyList, "SubscriptionManager");

        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(ownerQueryResponseMessage);

        assert (nextState == getReadyState());

        List<String> list = getReadyState().getDeleteUserToSubsystems().get("whatever");
        assert (null != list);
        assert (list.size() > 0);
        assert (list.get(0).equals("TopicManager"));

        BlockingQueue<Message> queue = getReadyState().getDeleteUserToQueue().get("whatever");
        assert (null != queue);
    }

    @Test
    public void testProcessOwnerQueryResponseMessageFinal () {
        BlockingQueue<Message> requester = new LinkedBlockingQueue<Message>();
        List<String> subsystems = new ArrayList<String>();
        subsystems.add("TopicManager");

        List<String> emptyList = new ArrayList<String>();

        getReadyState().getDeleteUserToQueue().put("whatever", requester);
        getReadyState().getDeleteUserToSubsystems().put("whatever", subsystems);

        OwnerQueryResponseMessage ownerQueryResponseMessage = new OwnerQueryResponseMessage(null, this,
                "whatever", emptyList, "TopicManager");

        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());
        when(getMockMiranda().getUserManager()).thenReturn(getMockUserManager());


        State nextState = getReadyState().processMessage(ownerQueryResponseMessage);

        assert (nextState == getReadyState());
        assert (null == getReadyState().getDeleteUserToSubsystems().get("whatever"));
        verify (getMockUserManager(), atLeastOnce()).sendDeleteUserMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.anyString());
    }


    @Test
    public void testProcessDeleteUserResponseMessageUnknownUser () {
        ReadyState.setLogger(getMockLogger());
        DeleteUserResponseMessage deleteUserResponseMessage = new DeleteUserResponseMessage(null,
                this, "whatever", Results.UserNotFound);
        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(deleteUserResponseMessage);

        assert (nextState == getReadyState());
        verify (getMockLogger(), atLeastOnce()).error(Matchers.anyString());
    }

    @Test
    public void testProcessDeleteUserResponseMessageSuccess () {
        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());
        when (getMockMiranda().getCluster()).thenReturn(getMockCluster());
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        getReadyState().getDeleteUserToQueue().put("whatever", queue);
        DeleteUserResponseMessage deleteUserResponseMessage = new DeleteUserResponseMessage(null,
                this, "whatever", Results.Success);

        State nextState = getReadyState().processMessage(deleteUserResponseMessage);

        assert (nextState == getReadyState());

        DeleteUserResponseMessage deleteUserResponseMessage2 = (DeleteUserResponseMessage)
                getMessage(Message.Subjects.DeleteUserResponse, queue);

        assert (deleteUserResponseMessage2.getResult() == Results.Success);

        verify (getMockCluster(), atLeastOnce()).sendDeleteUserMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.eq("whatever"));
    }

    public static final String TEST_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpjR9MH5cTEPIXR/0cLp/Lw3QDK4RMPIygL8Aqh0yQ/MOpQtXrBzwSph4N1NURg1tB3EuyCVGsTfSfrbR5nqsN5IiaJyBuvhThBLwHyKN+PEUQ/rB6qUyg+jcPigTfqj6gksNxnC6CmCJ6XpBOiBOORgFQvdISo7pOqxZKxmaTqwIDAQAB";

    @Test
    public void testProcessCreateUserMessage () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        UserObject userObject = new UserObject("whatever", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        CreateUserMessage createUserMessage = new CreateUserMessage(queue, this, user);

        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());
        when(getMockMiranda().getUserManager()).thenReturn(getMockUserManager());

        State nextState = getReadyState().processMessage(createUserMessage);

        assert (nextState == getReadyState());
        assert (getReadyState().getCreateUserToQueue().get("whatever") == queue);
        verify (getMockUserManager(), atLeastOnce()).sendCreateUserMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.eq(user));
    }

    @Test
    public void testProcessCreateUserResponseNotCreatedUnknownUser () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        CreateUserResponseMessage createUserResponseMessage = new CreateUserResponseMessage(null, this,
                user, Results.UserNotFound);

        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());

        ReadyState.setLogger(getMockLogger());

        State nextState = getReadyState().processMessage(createUserResponseMessage);

        assert (nextState == getReadyState());
        verify(getMockLogger(), atLeastOnce()).error(Matchers.anyString());
    }


    @Test
    public void testProcessCreateUserResponseSuccess () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        CreateUserResponseMessage createUserResponseMessage = new CreateUserResponseMessage(null, this,
                user, Results.Success);

        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());
        when(getMockMiranda().getCluster()).thenReturn(getMockCluster());

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        getReadyState().getCreateUserToQueue().put("whatever", queue);

        State nextState = getReadyState().processMessage(createUserResponseMessage);

        assert (nextState == getReadyState());

        verify (getMockCluster(), atLeastOnce()).sendNewUserMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.eq(user));

        CreateUserResponseMessage createUserResponseMessage2 = (CreateUserResponseMessage)
                getMessage(Message.Subjects.CreateUserResponse, queue);

        assert (createUserResponseMessage2.getResult() == Results.Success);
    }

    @Test
    public void testProcessUpdateUserMessage () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        UserObject userObject = new UserObject("whatever", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        UpdateUserMessage updateUserMessage = new UpdateUserMessage(queue, this, user);

        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());
        when(getMockMiranda().getUserManager()).thenReturn(getMockUserManager());

        State nextState = getReadyState().processMessage(updateUserMessage);

        assert (nextState == getReadyState());
        assert (getReadyState().getUpdateUserToQueue().get("whatever") == queue);
        verify (getMockUserManager(), atLeastOnce()).sendUpdateUserMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.eq(user));
    }

    @Test
    public void testProcessUpdateUserResponseMessageUnknownUser () throws MirandaException {
        ReadyState.setLogger(getMockLogger());
        UserObject userObject = new UserObject("whatever", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        UpdateUserResponseMessage updateUserResponseMessage = new UpdateUserResponseMessage(null,
                this, user, Results.UserNotFound);

        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());

        State nextState = getReadyState().processMessage(updateUserResponseMessage);

        assert (nextState == getReadyState());
        verify (getMockLogger(), atLeastOnce()).error(Matchers.anyString());
    }

    @Test
    public void testProcessUpdateUserResponseSuccess () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        UpdateUserResponseMessage updateUserResponseMessage = new UpdateUserResponseMessage(null,
                this, user, Results.Success);

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        getReadyState().getUpdateUserToQueue().put("whatever", queue);

        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());
        when(getMockMiranda().getCluster()).thenReturn(getMockCluster());

        State nextState = getReadyState().processMessage(updateUserResponseMessage);

        assert (nextState == getReadyState());
        verify(getMockCluster(), atLeastOnce()).sendUpdateUserMessage(Matchers.any(BlockingQueue.class), Matchers.any(),
                Matchers.any(User.class));

        UpdateUserResponseMessage updateUserResponseMessage2 = (UpdateUserResponseMessage)
            getMessage(Message.Subjects.UpdateUserResponse, queue);

        assert (updateUserResponseMessage2.getResult() == Results.Success);
        assert (getReadyState().getUpdateUserToQueue().get("whatever") == null);
    }

    @Test
    public void testProcessUserAddedMessage () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "Whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        UserAddedMessage userAddedMessage = new UserAddedMessage(null, this, user);

        when(getMockMiranda().getCurrentState()).thenReturn(getReadyState());
        when(getMockMiranda().getUserManager()).thenReturn(getMockUserManager());

        State nextState = getReadyState().processMessage(userAddedMessage);

        assert (nextState == getReadyState());

        verify (getMockUserManager(), atLeastOnce()).sendUserAddedMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.eq(user));
    }

    @Test
    public void testProcessUserUpdatedMessage () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "Whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        UserUpdatedMessage userUpdatedMessage = new UserUpdatedMessage(null, this, user);

        when (getMockMiranda().getCurrentState()).thenReturn(getReadyState());
        when (getMockMiranda().getUserManager()).thenReturn(getMockUserManager());

        State nextState = getReadyState().processMessage(userUpdatedMessage);

        assert (nextState == getReadyState());

        verify (getMockUserManager(), atLeastOnce()).sendUserUpdatedMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.eq(user));
    }

    @Test
    public void testProcessUserDeletedMessage () throws MirandaException {
        UserObject userObject = new UserObject("whatever", "Whatever", TEST_PUBLIC_KEY);
        User user = userObject.asUser();
        UserDeletedMessage userDeletedMessage = new UserDeletedMessage(null, this, "whatever");

        when (getMockMiranda().getCurrentState()).thenReturn(getReadyState());
        when (getMockMiranda().getUserManager()).thenReturn(getMockUserManager());

        State nextState = getReadyState().processMessage(userDeletedMessage);

        assert (nextState == getReadyState());

        verify (getMockUserManager(), atLeastOnce()).sendUserDeletedMessage(Matchers.any(BlockingQueue.class),
                Matchers.any(), Matchers.eq("whatever"));

    }
}
