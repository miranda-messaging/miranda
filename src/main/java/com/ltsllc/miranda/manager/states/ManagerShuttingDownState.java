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

package com.ltsllc.miranda.manager.states;


import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.manager.Manager;
import com.ltsllc.miranda.shutdown.ShutdownResponseMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;

import java.util.concurrent.BlockingQueue;

/**
 * Tell the associated file to shut down and wait for a reply.
 *
 * <p>
 *     Once the class gets a reply it sends a reply of is own and goes into the {@link StopState}.
 * </p>
 */
public class ManagerShuttingDownState extends ManagerState {
    private BlockingQueue<Message> requester;

    public BlockingQueue<Message> getRequester() {
        return requester;
    }

    public ManagerShuttingDownState(Manager manager, BlockingQueue<Message> requester) throws MirandaException {
        super(manager);

        this.requester = requester;
    }

    public State start () {
        getManager().getFile().sendShutdown(getManager().getQueue(), this);
        return this;
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
