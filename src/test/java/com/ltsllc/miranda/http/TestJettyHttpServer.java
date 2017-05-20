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

package com.ltsllc.miranda.http;

import com.ltsllc.miranda.servlet.objects.ServletMapping;
import com.ltsllc.miranda.servlet.status.StatusServlet;
import com.ltsllc.miranda.test.TestCase;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/17/2017.
 */
public class TestJettyHttpServer extends TestCase {
    @Mock
    private Server mockServer;

    @Mock
    private HandlerCollection mockHandlerCollection;

    private JettyHttpServer jettyHttpServer;

    public JettyHttpServer getJettyHttpServer() {
        return jettyHttpServer;
    }

    public HandlerCollection getMockHandlerCollection() {

        return mockHandlerCollection;
    }

    public Server getMockServer() {

        return mockServer;
    }

    public void reset () {
        super.reset();

        jettyHttpServer = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        this.mockHandlerCollection = mock(HandlerCollection.class);
        this.mockServer = mock(Server.class);
        this.jettyHttpServer = new JettyHttpServer(getMockServer(), getMockHandlerCollection());
    }

    @Test
    public void testConstructor () {
        assert (getJettyHttpServer().getCurrentState() instanceof HttpReadyState);
    }

    @Test
    public void testAddSevlets () {
        ServletMapping servletMapping = new ServletMapping("/servlets/test", StatusServlet.class);
        List<ServletMapping> list = new ArrayList<ServletMapping>();
        list.add(servletMapping);

        getJettyHttpServer().addServlets(list);

        verify(getMockHandlerCollection(), atLeastOnce()).addHandler(Matchers.any(Handler.class));
    }

    @Test
    public void testStartServer () {
        //
        // this cannot be tested
        //
        // getJettyHttpServer().startServer();

//        verify(getMockServer(), atLeastOnce()).setHandler(Matchers.any(Handler.class));
//        try {
//            verify(getMockServer(), atLeastOnce()).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
