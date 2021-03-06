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

package com.ltsllc.miranda.servlet.encrypt;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.servlet.enctypt.CreateKeyPairServlet;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestCreateKeyPairServlet extends TestCase {
    private CreateKeyPairServlet servlet;

    public CreateKeyPairServlet getServlet() {
        return servlet;
    }

    public void reset () throws Exception {
        super.reset();

        servlet = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        servlet = new CreateKeyPairServlet();
    }

    @Test
    public void testDoGet () {

    }
}
