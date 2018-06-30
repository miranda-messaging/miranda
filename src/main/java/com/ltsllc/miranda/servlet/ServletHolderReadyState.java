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

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.session.messages.CheckSessionResponseMessage;

/**
 * Created by Clark on 4/2/2017.
 */
public class ServletHolderReadyState extends State {
    public ServletHolder getServletHolder() {
        return (ServletHolder) getContainer();
    }

    public ServletHolderReadyState(ServletHolder servletHolder) throws MirandaException {
        super(servletHolder);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getServletHolder().getCurrentState();

        switch (message.getSubject()) {
            case CheckSessionResponse: {
                CheckSessionResponseMessage checkSessionResponseMessage = (CheckSessionResponseMessage) message;
                nextState = processCheckSessionResponseMessage(checkSessionResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processCheckSessionResponseMessage(CheckSessionResponseMessage checkSessionResponseMessage) {
        getServletHolder().setSessionAndAwaken(checkSessionResponseMessage.getSession());

        return getServletHolder().getCurrentState();
    }
}
