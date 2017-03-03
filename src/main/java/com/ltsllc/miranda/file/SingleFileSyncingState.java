package com.ltsllc.miranda.file;

import com.google.gson.Gson;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.util.Utils;
import com.ltsllc.miranda.node.GetFileMessage;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Clark on 2/11/2017.
 */
abstract public class SingleFileSyncingState extends State {
    abstract public Type getListType();
    abstract public State getReadyState();
    abstract public List getData();
    abstract public boolean contains(Object o);
    abstract public SingleFile getFile();
    abstract public String getName();

    private static Gson ourGson = new Gson();

    public SingleFileSyncingState (Consumer consumer) {
        super(consumer);
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case GetFileResponse: {
                GetFileResponseMessage getFileResponseMessage = (GetFileResponseMessage) message;
                nextState = processGetFileResponse (getFileResponseMessage);
                break;
            }

            case GetFile: {
                GetFileMessage getFileMessage = (GetFileMessage) message;
                nextState = processGetFileMessage (getFileMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    public State processGetFileResponse (GetFileResponseMessage getFileResponseMessage) {
        merge(getFileResponseMessage.getContents());

        return getReadyState();
    }

    public void merge (String hexString) {
        byte[] buffer = Utils.hexStringToBytes(hexString);
        String json = new String(buffer);
        List list = ourGson.fromJson(json, getListType());
        for (Object o1 : list) {
            if (!contains(o1))
                getData().add(o1);
        }
    }


    private State processGetFileMessage (GetFileMessage getFileMessage) {
        GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(getFile().getQueue(), this, getName(),
                getFile().getBytes());
        send(getFileMessage.getSender(), getFileResponseMessage);

        return this;
    }
}
