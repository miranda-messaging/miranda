package com.ltsllc.miranda.user;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.node.messages.GetFileMessage;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.messages.NewUserMessage;
import com.ltsllc.miranda.user.states.UsersFileReadyState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/24/2017.
 */
public class TestUsersFileReadyState extends TestCase {
    private UsersFileReadyState readyState;

    public UsersFileReadyState getReadyState() {
        return readyState;
    }

    public void reset () {
        super.reset();

        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        readyState = new UsersFileReadyState(getMockUsersFile());
    }

    @Test
    public void testProcessGetVersionMessage () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        GetVersionMessage getVersionMessage = new GetVersionMessage(null, this, queue);

        State nextState = getReadyState().processMessage(getVersionMessage);

        assert (nextState instanceof UsersFileReadyState);
        assert (contains(Message.Subjects.Version, queue));
    }

    @Test
    public void testProcessNewUserMessage () {
        User user = new User("whtever", "whatever");
        NewUserMessage newUserMessage = new NewUserMessage(null, this, user);

        State nextState = getReadyState().processMessage(newUserMessage);

        assert (nextState instanceof UsersFileReadyState);
        verify (getMockUsersFile(), atLeastOnce()).addUser(Matchers.eq(user));
    }

    @Test
    public void testProcessGetFileMessage () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        GetFileMessage getFileMessage = new GetFileMessage(queue, this, "whatever");

        when(getMockUsersFile().getBytes()).thenReturn("whatever".getBytes());

        State nextState = getReadyState().processMessage(getFileMessage);

        assert (nextState instanceof UsersFileReadyState);
        assert (contains(Message.Subjects.GetFileResponse, queue));
    }
}
