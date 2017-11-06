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
import com.ltsllc.miranda.servlet.enctypt.CreateKeyPairServlet;
import com.ltsllc.miranda.test.TestServlet;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Clark on 4/8/2017.
 */
public class TestCreateKeyPairServlet extends TestServlet {

    private CreateKeyPairServlet createKeyPairServlet;

    public CreateKeyPairServlet getCreateKeyPairServlet() {
        return createKeyPairServlet;
    }

    public void reset () throws MirandaException {
        super.reset();

        mockHttpServletResponse = null;
        mockHttpServletRequest = null;
        createKeyPairServlet = null;
    }

    @Before
    public void setup () throws MirandaException {
        reset();

        super.setup();

        mockHttpServletRequest = mock(HttpServletRequest.class);
        mockHttpServletResponse = mock(HttpServletResponse.class);
        createKeyPairServlet = new CreateKeyPairServlet();
    }

    public void setupMockCreateKeyPairHolder () {

    }
    @Test
    public void testDoGetSuccess () {
        StringServletOutputStream stringServletOutputStream = new StringServletOutputStream();

        try {
            when(getMockHttpServletResponse().getOutputStream()).thenReturn(stringServletOutputStream);
            getCreateKeyPairServlet().doGet(getMockHttpServletRequest(), getMockHttpServletResponse());
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }

        String result = stringServletOutputStream.getStringWriter().toString();
        assert (result.startsWith("{\"publicKey\""));
    }

    @Test
    public void testDoGetException () {
        Exception exception = null;

        try {
            IOException ioException = new IOException("test");
            when(getMockHttpServletResponse().getOutputStream()).thenThrow(ioException);

            getCreateKeyPairServlet().doGet(getMockHttpServletRequest(), getMockHttpServletResponse());
        } catch (Exception e) {
            exception = e;
        }

        assert (null != exception);
    }
}
