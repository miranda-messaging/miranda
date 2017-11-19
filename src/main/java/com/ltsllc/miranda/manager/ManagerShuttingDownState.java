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

package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.ShutdownResponseMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/26/2017.
 */
public class ManagerShuttingDownState extends State {
    private BlockingQueue<Message> requester;

    public BlockingQueue<Message> getRequester() {
        return requester;
    }

    public Manager getManager() {
        return (Manager) getContainer();
    }

    public ManagerShuttingDownState(Manager manager, BlockingQueue<Message> requester) throws MirandaException {
        super(manager);

        this.requester = requester;
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getManager().getCurrentState();

        switch (message.getSubject()) {
            case ShutdownResponse: {
                ShutdownResponseMessage shutdownResponseMessage = (ShutdownResponseMessage) message;
                nextState = processShutdownResponseMessage(shutdownResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    public State processShutdownResponseMessage(ShutdownResponseMessage shutdownResponseMessage) {
        ShutdownResponseMessage shutdownResponseMessage2 = new ShutdownResponseMessage(getManager().getQueue(), this,
                getManager().getName());

        send(getRequester(), shutdownResponseMessage2);

        return StopState.getInstance();
    }
}
