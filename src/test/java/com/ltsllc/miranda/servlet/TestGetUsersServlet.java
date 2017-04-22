package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.servlet.holder.UserHolder;
import com.ltsllc.miranda.servlet.user.GetUsersServlet;
import com.ltsllc.miranda.test.TestServlet;
import com.ltsllc.miranda.user.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 4/8/2017.
 */
public class TestGetUsersServlet extends TestServlet {
    @Mock
    private UserHolder mockUserHolder;

    private GetUsersServlet getUsersServlet;

    public GetUsersServlet getUsersServlet() {
        return getUsersServlet;
    }

    public UserHolder getMockUsersHolder() {
        return mockUserHolder;
    }

    public void reset () {
        super.reset();

        mockUserHolder = null;
        getUsersServlet = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        mockUserHolder = mock(UserHolder.class);
        getUsersServlet = new GetUsersServlet();
    }

    public void setupMockUsersHolder () {
        UserHolder.setInstance(getMockUsersHolder());
    }

    @Test
    public void testDoGet () {
        User user = new User("whatever", "whatever");
        List<User> users = new ArrayList<User>();
        users.add(user);

        StringServletOutputStream stringServletOutputStream = new StringServletOutputStream();

        setupMockUsersHolder();

        try {
            when(getMockUsersHolder().getUserList()).thenReturn(users);
            when(getMockHttpServletResponse().getOutputStream()).thenReturn(stringServletOutputStream);

            getUsersServlet().doGet(getMockHttpServletRequest(), getMockHttpServletResponse());
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }
}
