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
