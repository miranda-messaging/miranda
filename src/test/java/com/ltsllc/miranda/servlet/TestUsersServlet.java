package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.servlet.holder.UsersHolder;
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
public class TestUsersServlet extends TestServlet {
    @Mock
    private UsersHolder mockUsersHolder;

    private UsersServlet usersServlet;

    public UsersServlet getUsersServlet() {
        return usersServlet;
    }

    public UsersHolder getMockUsersHolder() {
        return mockUsersHolder;
    }

    public void reset () {
        super.reset();

        mockUsersHolder = null;
        usersServlet = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        mockUsersHolder = mock(UsersHolder.class);
        usersServlet = new UsersServlet();
    }

    public void setupMockUsersHolder () {
        UsersHolder.setInstance(getMockUsersHolder());
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
