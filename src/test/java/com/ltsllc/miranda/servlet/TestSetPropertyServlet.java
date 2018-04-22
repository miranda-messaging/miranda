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
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.servlet.property.SetPropertyServlet;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/24/2017.
 */
public class TestSetPropertyServlet extends TestCase {
    @Mock
    private HttpServletRequest mockServletRequest;

    @Mock
    private HttpServletResponse mockServletResponse;

    private SetPropertyServlet setPropertyServlet;

    public SetPropertyServlet getSetPropertyServlet() {
        return setPropertyServlet;
    }

    public HttpServletResponse getMockServletResponse() {
        return mockServletResponse;
    }

    public HttpServletRequest getMockServletRequest() {

        return mockServletRequest;
    }

    public void reset () throws Exception {
        super.reset();

        mockServletRequest = null;
        mockServletResponse = null;
        setPropertyServlet = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        setuplog4j();

        mockServletRequest = mock(HttpServletRequest.class);
        mockServletResponse = mock(HttpServletResponse.class);
        setPropertyServlet = new SetPropertyServlet();
    }

    @Test
    public void testDoPost () {
        setupMockProperties();
        List<String> names = new ArrayList<String>();
        names.add(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS);
        Enumeration<String> listEnumeration = Collections.enumeration(names);

        when(getMockServletRequest().getParameterNames()).thenReturn(listEnumeration);
        when(getMockServletRequest().getParameter(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS)).thenReturn("whatever");
        when(getMockProperties().getProperty(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS)).thenReturn(MirandaProperties.DEFAULT_CERTIFICATE_ALIAS);

        try {
            getSetPropertyServlet().doPost(getMockServletRequest(), getMockServletResponse());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        verify(getMockProperties(), atLeastOnce()).setProperty(Matchers.eq(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS),
                Matchers.eq("whatever"));
    }
}
