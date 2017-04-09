package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.servlet.holder.CreateUserHolder;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.test.TestServlet;
import com.ltsllc.miranda.user.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import javax.servlet.ServletException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 4/8/2017.
 */
public class TestCreateUserServlet extends TestServlet {
    @Mock
    private CreateUserHolder mockCreateUserHolder;

    private CreateUserServlet createUserServlet;

    public CreateUserServlet getCreateUserServlet() {
        return createUserServlet;
    }

    public CreateUserHolder getMockCreateUserHolder() {
        return mockCreateUserHolder;
    }

    public void reset () {
        super.reset();

        mockCreateUserHolder = null;
        createUserServlet = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        mockCreateUserHolder = mock(CreateUserHolder.class);
        createUserServlet = new CreateUserServlet();
    }

    public void setupMockCreateUserHolder () {
        CreateUserHolder.setInstance(getMockCreateUserHolder());
    }

    @Test
    public void testDoPostSuccess () {
        String s = "{ \"name\" : \"admin\", \"description\" : \"whatever\", \"publicKey\" : \"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMOinA1ha2eTP/9KwszAhYfbNJiapjz8/3mgTnglRxi7Hi1cJSTODks7SKzzkDdM+GsQctOTMYMA3hittfuU3PiCv0hmDotwpdjvW+5r2xJ+DuFV7dSZOEVMeMJlO2MJEPFS0KPI/DUdy8+A//yu4qPzzC5A6U1zJ1jcQNzl/WUwIDAQAB\" }";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(s.getBytes());
        StringServletInputStream stringServletInputStream = new StringServletInputStream(byteArrayInputStream);
        StringServletOutputStream stringServletOutputStream = new StringServletOutputStream();
        setupMockCreateUserHolder();

        try {
            when(getMockCreateUserHolder().createUser(Matchers.any(User.class))).thenReturn(true);
            when(getMockHttpServletResponse().getOutputStream()).thenReturn(stringServletOutputStream);
            when(getMockHttpServletRequest().getInputStream()).thenReturn(stringServletInputStream);

            getCreateUserServlet().doPost(getMockHttpServletRequest(), getMockHttpServletResponse());
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }

        String result = stringServletOutputStream.getStringWriter().toString();
        assert (result.startsWith("{\"result\":\"success\""));
    }

    @Test
    public void testDoPostDuplicate () {
        String s = "{ \"name\" : \"admin\", \"description\" : \"whatever\", \"publicKey\" : \"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMOinA1ha2eTP/9KwszAhYfbNJiapjz8/3mgTnglRxi7Hi1cJSTODks7SKzzkDdM+GsQctOTMYMA3hittfuU3PiCv0hmDotwpdjvW+5r2xJ+DuFV7dSZOEVMeMJlO2MJEPFS0KPI/DUdy8+A//yu4qPzzC5A6U1zJ1jcQNzl/WUwIDAQAB\" }";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(s.getBytes());
        StringServletInputStream stringServletInputStream = new StringServletInputStream(byteArrayInputStream);
        StringServletOutputStream stringServletOutputStream = new StringServletOutputStream();
        setupMockCreateUserHolder();

        try {
            when(getMockCreateUserHolder().createUser(Matchers.any(User.class))).thenReturn(false);
            when(getMockHttpServletResponse().getOutputStream()).thenReturn(stringServletOutputStream);
            when(getMockHttpServletRequest().getInputStream()).thenReturn(stringServletInputStream);

            getCreateUserServlet().doPost(getMockHttpServletRequest(), getMockHttpServletResponse());
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }

        String result = stringServletOutputStream.getStringWriter().toString();
        String expectedResult = "{\"result\":\"failure\",\"additionalInfo\":\"duplicate user\"}";

        assert (result.startsWith(expectedResult));
    }

    @Test
    public void testDoPostException () {
        String s = "{ \"name\" : \"admin\", \"description\" : \"whatever\", \"publicKey\" : \"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMOinA1ha2eTP/9KwszAhYfbNJiapjz8/3mgTnglRxi7Hi1cJSTODks7SKzzkDdM+GsQctOTMYMA3hittfuU3PiCv0hmDotwpdjvW+5r2xJ+DuFV7dSZOEVMeMJlO2MJEPFS0KPI/DUdy8+A//yu4qPzzC5A6U1zJ1jcQNzl/WUwIDAQAB\" }";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(s.getBytes());
        StringServletInputStream stringServletInputStream = new StringServletInputStream(byteArrayInputStream);
        StringServletOutputStream stringServletOutputStream = new StringServletOutputStream();
        setupMockCreateUserHolder();
        IOException ioException = new IOException("test");
        IOException exception = null;

        try {
            when(getMockCreateUserHolder().createUser(Matchers.any(User.class))).thenReturn(false);
            when(getMockHttpServletResponse().getOutputStream()).thenThrow(ioException);
            when(getMockHttpServletRequest().getInputStream()).thenReturn(stringServletInputStream);

            getCreateUserServlet().doPost(getMockHttpServletRequest(), getMockHttpServletResponse());
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            exception = e;
        }

        assert (exception != null);
    }
}
