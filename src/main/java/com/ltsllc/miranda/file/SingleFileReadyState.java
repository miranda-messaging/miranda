package com.ltsllc.miranda.file;

import com.google.gson.Gson;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.node.GetFileMessage;
import com.ltsllc.miranda.util.Utils;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Clark on 2/10/2017.
 */
abstract public class SingleFileReadyState extends State {
    abstract public Version getVersion();
    abstract public Type getListType();
    abstract public void write();
    abstract public boolean contains(Object o);
    abstract public SingleFile getFile();
    abstract public void add(Object o);
    abstract public String getName();
    abstract public List<Perishable> getPerishables();

    private static Logger logger = Logger.getLogger(SingleFileReadyState.class);
    private static Gson ourGson = new Gson();

    public SingleFileReadyState (Consumer consumer) {
        super(consumer);
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case GetFileResponse: {
                GetFileResponseMessage getFileResponseMessage = (GetFileResponseMessage) message;
                nextState = processGetFileResponseMessage (getFileResponseMessage);
                break;
            }

            case GetFile: {
                GetFileMessage getFileMessage = (GetFileMessage) message;
                nextState = processGetFileMessage(getFileMessage);
                break;
            }

            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            default :
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    public State processGetFileResponseMessage (GetFileResponseMessage getFileResponseMessage) {
        String hexString = getFileResponseMessage.getContents();
        byte[] buffer = Utils.hexStringToBytes(hexString);
        String json = new String(buffer);
        List list = ourGson.fromJson(json, getListType());
        merge(list);
        write();

        return this;
    }

    public void merge (List list) {
        for (Object o : list) {
            if (!contains(o))
                add(o);
        }
    }

    private State processGetFileMessage(GetFileMessage getFileMessage) {
        GetFileResponseMessage getFileResponseMessage = null;

        if (null == getFile().getData()) {
            getFileResponseMessage = new GetFileResponseMessage(getFile().getQueue(),this, getFileMessage.getFile());
        }
        else {
            getFileResponseMessage = new GetFileResponseMessage(getFile().getQueue(), this, getFileMessage.getFile(), getFile().getBytes());
        }

        send (getFileMessage.getSender(), getFileResponseMessage);

        return this;
    }


    private State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) { return this; }
}
