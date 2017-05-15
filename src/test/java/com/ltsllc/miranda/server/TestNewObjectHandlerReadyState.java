package com.ltsllc.miranda.server;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.user.NewUserHandler;
import com.ltsllc.miranda.user.states.NewUserHandlerReadyState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestNewObjectHandlerReadyState extends TestCase {
    @Mock
    private Consumer mockConsumer;

    @Mock
    private NewUserHandler mockNewUserHandler;

    private NewObjectHandlerReadyState readyState;

    public NewUserHandler getMockNewUserHandler() {
        return mockNewUserHandler;
    }

    public Consumer getMockConsumer() {

        return mockConsumer;
    }

    public NewObjectHandlerReadyState getReadyState() {
        return readyState;
    }

    public void reset () {
        super.reset();

        mockNewUserHandler = null;
        mockConsumer = null;
        readyState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        mockNewUserHandler = mock(NewUserHandler.class);
        mockConsumer = mock(Consumer.class);
        readyState = new NewUserHandlerReadyState(mockConsumer, getMockUsersFile(), mockNewUserHandler);
    }


}
