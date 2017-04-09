package com.ltsllc.miranda.servlet.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.servlet.holder.CreateUserHolder;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.messages.DuplicateUserMessage;
import com.ltsllc.miranda.user.messages.UserCreatedMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 4/8/2017.
 */
public class TestCreateUserHolderReadyState extends TestCase {
    @Mock
    private CreateUserHolder mockCreateUserHolder;

    private CreateUserHolderReadyState createUserHolderReadyState;

    public CreateUserHolderReadyState getCreateUserHolderReadyState() {
        return createUserHolderReadyState;
    }

    public CreateUserHolder getMockCreateUserHolder() {
        return mockCreateUserHolder;
    }

    public void reset () {
        super.reset();

        mockCreateUserHolder = null;
        createUserHolderReadyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        mockCreateUserHolder = mock(CreateUserHolder.class);
        createUserHolderReadyState = new CreateUserHolderReadyState(mockCreateUserHolder);
    }

    @Test
    public void testProcessUserCreatedMessage () {
        UserCreatedMessage userCreatedMessage = new UserCreatedMessage(null, this);

        when(getMockCreateUserHolder().getCurrentState()).thenReturn(getCreateUserHolderReadyState());

        State nextState = getCreateUserHolderReadyState().processMessage(userCreatedMessage);

        assert (nextState == getCreateUserHolderReadyState());
        verify(getMockCreateUserHolder(), atLeastOnce()).setUserCreatedAndWakeup(Matchers.eq(true));
    }

    @Test
    public void testProcessDuplicateUserMessage () {
        DuplicateUserMessage duplicateUserMessage = new DuplicateUserMessage(null, this);

        when(getMockCreateUserHolder().getCurrentState()).thenReturn(getCreateUserHolderReadyState());

        State nextState = getCreateUserHolderReadyState().processMessage(duplicateUserMessage);

        assert (nextState == getCreateUserHolderReadyState());
        verify(getMockCreateUserHolder(), atLeastOnce()).setUserCreatedAndWakeup(Matchers.eq(false));
    }
}
