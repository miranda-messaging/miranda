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

package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.servlet.user.ListUsersServlet;
import com.ltsllc.miranda.servlet.user.UserHolder;
import com.ltsllc.miranda.test.TestServlet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 4/8/2017.
 */
public class TestGetUsersServlet extends TestServlet {
    public static class LocalServletInputStream extends ServletInputStream {
        private ByteArrayInputStream byteArrayInputStream;
        private int lastRead;

        public LocalServletInputStream (ByteArrayInputStream byteArrayInputStream) {
            this.byteArrayInputStream = byteArrayInputStream;
        }

        public int read () {
            lastRead = byteArrayInputStream.read();
            return lastRead;
        }

        public boolean isReady () {
            return true;
        }

        public void setReadListener (ReadListener readListener) {}

        public boolean isFinished () {
            return lastRead == -1;
        }
    }

    @Mock
    private UserHolder mockUserHolder;

    private ListUsersServlet getUsersServlet;

    public ListUsersServlet getUsersServlet() {
        return getUsersServlet;
    }

    public UserHolder getMockUsersHolder() {
        return mockUserHolder;
    }

    public void reset () throws Exception {
        super.reset();

        mockUserHolder = null;
        getUsersServlet = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        mockUserHolder = mock(UserHolder.class);
        getUsersServlet = new ListUsersServlet();
    }

    public void setupMockUsersHolder () {
        UserHolder.setInstance(getMockUsersHolder());
    }

    @Test
    public void testDoPost () {
        User user = new User("whatever", "whatever");
        String input = "{ \"session\" : \"1234\"}";
        byte[] data = input.getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        LocalServletInputStream localServletInputStream = new LocalServletInputStream(byteArrayInputStream);

        List<User> users = new ArrayList<User>();
        users.add(user);

        StringServletOutputStream stringServletOutputStream = new StringServletOutputStream();

        setupMockUsersHolder();

        try {
            when(getMockUsersHolder().getUserList()).thenReturn(users);
            when(getMockHttpServletResponse().getOutputStream()).thenReturn(stringServletOutputStream);
            when(getMockHttpServletRequest().getInputStream()).thenReturn(localServletInputStream);

            getUsersServlet().doPost(getMockHttpServletRequest(), getMockHttpServletResponse());
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }
}
