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

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.http.messages.SetupServletsMessage;
import com.ltsllc.miranda.http.messages.StartHttpServerMessage;
import com.ltsllc.miranda.http.states.HttpReadyState;
import com.ltsllc.miranda.test.TestCase;
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
public class TestHttpReadyState extends TestCase {
    @Mock
    private HttpServer mockHttpServer;

    private HttpReadyState httpReadyState;

    public void reset () throws Exception {
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
    public void setup () throws Exception {
        reset();

        super.setup();

        this.mockHttpServer = mock(HttpServer.class);

        this.httpReadyState = new HttpReadyState(mockHttpServer);
    }

    @Test
    public void testProcessSetupServletsMessage () throws MirandaException {
        List<ServletMapping> mappings = new ArrayList<ServletMapping>();
        SetupServletsMessage setupServletsMessage = new SetupServletsMessage(null, this, mappings);

        getHttpReadyState().processMessage(setupServletsMessage);

        verify(getMockHttpServer(), atLeastOnce()).addServlets(Matchers.anyList());
    }

    @Test
    public void testProcessStartHttpServerMessage () throws MirandaException {
        StartHttpServerMessage startHttpServerMessage = new StartHttpServerMessage(null, this);

        getHttpReadyState().processMessage(startHttpServerMessage);

        verify(getMockHttpServer(), atLeastOnce()).startServer();
    }
}
