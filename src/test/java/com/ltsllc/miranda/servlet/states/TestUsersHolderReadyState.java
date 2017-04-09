package com.ltsllc.miranda.servlet.states;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.holder.UsersHolder;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.user.messages.GetUsersResponseMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 4/8/2017.
 */
public class TestUsersHolderReadyState extends TestCase {
    @Mock
    private UsersHolder mockUsersHolder;

    private UsersHolderReadyState usersHolderReadyState;

    public UsersHolder getMockUsersHolder() {
        return mockUsersHolder;
    }

    public UsersHolderReadyState getUsersHolderReadyState() {
        return usersHolderReadyState;
    }

    public void reset() {
        super.reset();

        mockUsersHolder = null;
        usersHolderReadyState = null;
    }

    @Before
    public void setup() {
        reset();

        super.setup();

        mockUsersHolder = mock(UsersHolder.class);
        usersHolderReadyState = new UsersHolderReadyState(mockUsersHolder);
    }

    @Test
    public void testProcessGetUsersResponseMessage() {
        User user = new User("whatever", "whatever");
        List<User> users = new ArrayList<User>();
        users.add(user);
        GetUsersResponseMessage getUsersResponseMessage = new GetUsersResponseMessage(null, this, users);

        when(getMockUsersHolder().getCurrentState()).thenReturn(getUsersHolderReadyState());

        State nextState = getUsersHolderReadyState().processMessage(getUsersResponseMessage);

        assert (nextState == getUsersHolderReadyState());
        verify(getMockUsersHolder(), atLeastOnce()).setUsersListAndAwaken(Matchers.eq(users));
    }
}
