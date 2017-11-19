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

package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.messages.ConnectFailedMessage;
import com.ltsllc.miranda.network.messages.ConnectSucceededMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.messages.RetryMessage;
import org.apache.log4j.Logger;

/**
 * A node that can't connect to a remote system enters this state.
 * <p>
 * <p>
 * This state assumes that the timer has not already been notified.
 * </p>
 */
public class RetryingState extends NodeState {
    private Logger logger = Logger.getLogger(RetryingState.class);

    private int retryCount;

    public int getRetryCount() {
        return retryCount;
    }

    public void incrementRetries() {
        this.retryCount++;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public RetryingState(Node node, Network network) throws MirandaException {
        super(node, network);
    }

    public static final long INITIAL_DELAY = 1000; // one second

    public State start() {
        retryCount = 0;

        RetryMessage retryMessage = new RetryMessage(getNode().getQueue(), this);
        Miranda.timer.sendScheduleOnce(INITIAL_DELAY, getNode().getQueue(), retryMessage);

        return this;
    }

    public State processMessage(Message m) throws MirandaException {
        State nextState = this;

        switch (m.getSubject()) {
            case Retry: {
                RetryMessage retryMessage = (RetryMessage) m;
                nextState = processRetryMessage(retryMessage);
                break;
            }

            case ConnectSucceeded: {
                ConnectSucceededMessage connectSucceededMessage = (ConnectSucceededMessage) m;
                nextState = processConnectSuceeededMessage(connectSucceededMessage);
                break;
            }

            case ConnectFailed: {
                ConnectFailedMessage connectFailedMessage = (ConnectFailedMessage) m;
                nextState = processConnectFailedMessage(connectFailedMessage);
                break;
            }

            default: {
                nextState = super.processMessage(m);
                break;
            }
        }

        return nextState;
    }

    private State processRetryMessage(RetryMessage retryMessage) {
        getNetwork().sendConnect(getNode().getQueue(), this, getNode().getDns(), getNode().getPort());

        return this;
    }

    public static final long MAX_TIME = 60000; // one minute
    public static final int MAX_RETRY_COUNT = 10;

    private State processConnectFailedMessage(ConnectFailedMessage connectFailedMessage) {
        incrementRetries();
        int retryCount = getRetryCount() - 1;
        if (retryCount > MAX_RETRY_COUNT)
            retryCount = MAX_RETRY_COUNT;

        long delay = INITIAL_DELAY << retryCount;
        if (delay > MAX_TIME)
            delay = MAX_TIME;

        RetryMessage retryMessage = new RetryMessage(getNode().getQueue(), this);
        Miranda.timer.sendScheduleOnce(delay, getNode().getQueue(), retryMessage);

        String message = "Failed to connect to " + getNode().getDns() + ":" + getNode().getPort() + ".  Will "
                + "try agian in " + delay + "msec.";
        logger.info(message);

        return this;
    }


    private State processConnectSuceeededMessage(ConnectSucceededMessage connectSucceededMessage) throws MirandaException {
        getNode().setHandle(connectSucceededMessage.getHandle());

        JoiningState joiningState = new JoiningState(getNode(), getNetwork());
        return joiningState;
    }
}
