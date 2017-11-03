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

package com.ltsllc.miranda.network;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.PanicMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

/**
 * An abstract class to make switching between netty, mina and sockets easier
 */
abstract public class Handle {
    abstract public void send (WireMessage wireMessage) throws NetworkException;
    abstract public void close ();
    abstract public void panic ();

    private static Gson ourGson = new Gson();

    private BlockingQueue<Message> queue;

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<Message> queue) {
        this.queue = queue;
    }

    public Handle (BlockingQueue<Message> queue) {
        this.queue = queue;
    }

    public static WireMessage jsonToWireMessage (WireMessage wireMessage, String json) throws NetworkException {
        try {
            Type messageType = Handle.class.forName(wireMessage.getClassName());
            return ourGson.fromJson(json, messageType);
        } catch (ClassNotFoundException e) {
            throw new NetworkException (wireMessage.getClassName(), NetworkException.Errors.ClassNotFound);
        }
    }

    public static WireMessage jsonToWireMessage (String json) {
        return ourGson.fromJson(json, WireMessage.class);
    }

    public static WireMessage jsonToWireMessageTwoPass (String json) throws NetworkException {
        WireMessage firstPass = jsonToWireMessage(json);
        return jsonToWireMessage(firstPass, json);
    }

    /**
     * This method is called when a new message is received from the network.
     *
     * <p>
     *     The base behavior is simply to put the message on the queue for the handle.
     * </p>
     *
     * @param wireMessage The new message.
     */
    public void deliver (WireMessage wireMessage) {
        NetworkMessage message = new NetworkMessage(null,this, wireMessage);
        try {
            getQueue().put(message);
        } catch (InterruptedException e) {
            Panic panic = new Panic ("Exception trying to sendToMe mesage", e, Panic.Reasons.ExceptionSendingMessage);
            Miranda.getInstance().panic(panic);
        }
    }

    public void sendPanicMessage (BlockingQueue<Message> senderQueue, Object sender, boolean ignoreExceptions) {
        PanicMessage panicMessage = new PanicMessage(senderQueue, sender);
        try {
            getQueue().put(panicMessage);
        } catch (InterruptedException e) {
            if (!ignoreExceptions) {
                Panic panic = new Panic("Exception trying to send message", e, Panic.Reasons.ExceptionSendingMessage);
                Miranda.getInstance().panic(panic);
            }
        }
    }
}
