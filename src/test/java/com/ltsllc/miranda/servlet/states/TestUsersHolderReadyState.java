package com.ltsllc.miranda.servlet.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.holder.UserHolder;
import com.ltsllc.miranda.servlet.user.UserHolderReadyState;
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
    private UserHolder mockUserHolder;

    private UserHolderReadyState usersHolderReadyState;

    public UserHolder getMockUserHolder() {
        return mockUserHolder;
    }

    public UserHolderReadyState getUserHolderReadyState() {
        return usersHolderReadyState;
    }

    public void reset() {
        super.reset();

        mockUserHolder = null;
        usersHolderReadyState = null;
    }

    @Before
    public void setup() {
        reset();

        super.setup();

        mockUserHolder = mock(UserHolder.class);
        usersHolderReadyState = new UserHolderReadyState(mockUserHolder);
    }

    @Test
    public void testProcessGetUsersResponseMessage() {
        User user = new User("whatever", "whatever");
        List<User> users = new ArrayList<User>();
        users.add(user);
        GetUsersResponseMessage getUsersResponseMessage = new GetUsersResponseMessage(null, this, users);

        when(getMockUserHolder().getCurrentState()).thenReturn(getUserHolderReadyState());

        State nextState = getUserHolderReadyState().processMessage(getUsersResponseMessage);

        assert (nextState == getUserHolderReadyState());
        verify(getMockUserHolder(), atLeastOnce()).setUsersAndAwaken(Matchers.eq(users));
    }
}
