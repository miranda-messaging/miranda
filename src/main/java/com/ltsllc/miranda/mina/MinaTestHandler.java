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

package com.ltsllc.miranda.mina;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * Test mina's ability to accept new connections
 */
public class MinaTestHandler extends IoHandlerAdapter {
    private static Logger logger = Logger.getLogger(MinaTestHandler.class);

    private String testMessage;
    private boolean successfulTest;

    public boolean isSuccessfulTest() {
        return successfulTest;
    }

    public void setSuccessfulTest(boolean successfulTest) {
        this.successfulTest = successfulTest;
    }

    public String getTestMessage() {
        return testMessage;
    }

    public void setTestMessage(String testMessage) {
        this.testMessage = testMessage;
    }

    public MinaTestHandler (String testMessage) {
        setTestMessage(testMessage);
        setSuccessfulTest(false);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        logger.info("Got connection from " + session.getRemoteAddress());
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (getTestMessage().equals(message.toString())) {
            setSuccessfulTest(true);
        }
    }
}
