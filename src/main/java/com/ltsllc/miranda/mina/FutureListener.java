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

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.messages.ConnectFailedMessage;
import com.ltsllc.miranda.network.messages.ConnectSucceededMessage;
import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/11/2017.
 */
public class FutureListener implements IoFutureListener<ConnectFuture> {
    private static Logger logger = Logger.getLogger(FutureListener.class);

    private BlockingQueue<Message> notify;
    private int handleID;
    private Network network;
    private Handle handle;

    public int getHandleID() {
        return handleID;
    }

    public BlockingQueue<Message> getNotify() {
        return notify;
    }

    public Network getNetwork() {
        return network;
    }

    public Handle getHandle() {
        return handle;
    }

    public FutureListener(BlockingQueue<Message> notify, int handleID, Network network, Handle handle) {
        this.notify = notify;
        this.handleID = handleID;
        this.network = network;
        this.handle = handle;
    }

    public void operationComplete(ConnectFuture connectFuture) {
        if (connectFuture.isConnected()) {
            logger.info("connect to " + connectFuture.getSession().getRemoteAddress() + " completed successfully");
            sendConnectSuceeded();
            getNetwork().mapHandle(getHandleID(), getHandle());
        } else {
            logger.info("connect failed");
            sendConnectFailed(connectFuture.getException());
        }
    }

    public void sendConnectSuceeded() {
        ConnectSucceededMessage connectSucceededMessage = new ConnectSucceededMessage(null, this, getHandleID());
        send(connectSucceededMessage);
    }

    public void sendConnectFailed(Throwable throwable) {
        ConnectFailedMessage connectFailedMessage = new ConnectFailedMessage(null, this, throwable);
        send(connectFailedMessage);
    }

    public void send(Message message) {
        try {
            getNotify().put(message);
        } catch (InterruptedException e) {
            Panic panic = new Panic("Interrupted while sending meesage", e, Panic.Reasons.ExceptionSendingMessage);
            Miranda.getInstance().panic(panic);
        }
    }
}
