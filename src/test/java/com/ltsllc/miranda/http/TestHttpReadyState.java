package com.ltsllc.miranda.http;

import com.ltsllc.miranda.servlet.objects.ServletMapping;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 3/17/2017.
 */
public class TestHttpReadyState extends TestCase {
    @Mock
    private HttpServer mockHttpServer;

    private HttpReadyState httpReadyState;

    public void reset () {
        super.reset();

        this.mockHttpServer = null;
        this.httpReadyState = null;
    }

    public HttpReadyState getHttpReadyState() {
        return httpReadyState;
    }

    public HttpServer getMockHttpServer() {

        return mockHttpServer;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        this.mockHttpServer = mock(HttpServer.class);

        this.httpReadyState = new HttpReadyState(mockHttpServer);
    }

    @Test
    public void testProcessSetupServletsMessage () {
        ServletMapping[] servletMappings = new ServletMapping[1];
        SetupServletsMessage setupServletsMessage = new SetupServletsMessage(null, this, servletMappings);

        getHttpReadyState().processMessage(setupServletsMessage);

        verify(getMockHttpServer(), atLeastOnce()).addServlets(Matchers.anyList());
    }

    @Test
    public void testProcessStartHttpServerMessage () {
        StartHttpServerMessage startHttpServerMessage = new StartHttpServerMessage(null, this);

        getHttpReadyState().processMessage(startHttpServerMessage);

        verify(getMockHttpServer(), atLeastOnce()).startServer();
    }
}
