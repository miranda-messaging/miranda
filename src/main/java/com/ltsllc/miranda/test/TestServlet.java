package com.ltsllc.miranda.test;

import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;

/**
 * Created by Clark on 4/8/2017.
 */
public class TestServlet extends TestCase {
    @Mock
    protected HttpServletRequest mockHttpServletRequest;
    @Mock
    protected HttpServletResponse mockHttpServletResponse;

    public HttpServletRequest getMockHttpServletRequest() {
        return mockHttpServletRequest;
    }

    public HttpServletResponse getMockHttpServletResponse() {
        return mockHttpServletResponse;
    }

    public void reset () {
        super.reset();

        mockHttpServletRequest = null;
        mockHttpServletResponse = null;
    }

    public void setup () {
        super.setup();

        mockHttpServletRequest = mock(HttpServletRequest.class);
        mockHttpServletResponse = mock(HttpServletResponse.class);
    }
}
