package com.ltsllc.miranda.network;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.NetworkMessage;
import com.ltsllc.miranda.node.WireMessage;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

/**
 * An abstract class to make switching between netty and sockets easier
 */
abstract public class Handle {
    abstract public void send (SendMessageMessage sendMessageMessage) throws NetworkException;
    abstract public void close (CloseMessage disconnectMessage);
    abstract public void panic ();

    private static Gson ourGson = new Gson();

    private BlockingQueue<Message> queue;

    public BlockingQueue<Message> getQueue() {
        return queue;
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

    public void deliver (WireMessage wireMessage) {
        NetworkMessage message = new NetworkMessage(null,this, wireMessage);
        try {
            getQueue().put(message);
        } catch (InterruptedException e) {
            Panic panic = new Panic ("Exception trying to send mesage", e, Panic.Reasons.ExceptionSendingMessage);
            Miranda.getInstance().panic(panic);
        }
    }

}
