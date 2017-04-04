package com.ltsllc.miranda.file.states;

import com.google.gson.Gson;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.Subscriber;
import com.ltsllc.miranda.file.messages.AddSubscriberMessage;
import com.ltsllc.miranda.file.messages.GetFileResponseMessage;
import com.ltsllc.miranda.file.messages.RemoveSubscriberMessage;
import com.ltsllc.miranda.miranda.GarbageCollectionMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.StopMessage;
import com.ltsllc.miranda.node.messages.GetFileMessage;
import com.ltsllc.miranda.util.Utils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/10/2017.
 */
abstract public class SingleFileReadyState extends MirandaFileReadyState {
    abstract public Version getVersion();
    abstract public Type getListType();
    abstract public void write();
    abstract public boolean contains(Object o);
    abstract public void add(Object o);
    abstract public String getName();
    abstract public List<Perishable> getPerishables();

    private static Logger logger = Logger.getLogger(SingleFileReadyState.class);
    private static Gson ourGson = new Gson();


    public SingleFileReadyState (SingleFile file) {
        super(file);
    }

    public SingleFile getFile () {
        return (SingleFile) getContainer();
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case Load: {
                LoadMessage loadMessage = (LoadMessage) message;
                nextState = processLoadMessage(loadMessage);
                break;
            }

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

            case WriteSucceeded: {
                getFile().setDirty(false);
                break;
            }

            case Stop: {
                StopMessage stopMessage = (StopMessage) message;
                nextState = processStopMessage(stopMessage);
                break;
            }

            case AddSubscriber: {
                AddSubscriberMessage addSubscriberMessage = (AddSubscriberMessage) message;
                nextState = processAddSubscriberMessage(addSubscriberMessage);
                break;
            }

            case RemoveSubscriber: {
                RemoveSubscriberMessage removeSubscriberMessage = (RemoveSubscriberMessage) message;
                nextState = processRemoveSubscriberMessage(removeSubscriberMessage);
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

        try {
            byte[] buffer = Utils.hexStringToBytes(hexString);
            String json = new String(buffer);
            List list = ourGson.fromJson(json, getListType());
            merge(list);
            write();
        } catch (IOException e) {
            Panic panic = new Panic("Excepion loading file", e, Panic.Reasons.ExceptionLoadingFile);
            Miranda.getInstance().panic(panic);
        }

        return getFile().getCurrentState();
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
            getFileResponseMessage = new GetFileResponseMessage(getFile().getQueue(),this, getFileMessage.getFilename());
        }
        else {
            getFileResponseMessage = new GetFileResponseMessage(getFile().getQueue(), this, getFileMessage.getFilename(), getFile().getBytes());
        }

        send (getFileMessage.getSender(), getFileResponseMessage);

        return this;
    }


    private State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        getFile().performGarbageCollection();
        return this;
    }

    private State processLoadMessage (LoadMessage loadMessage) {
        getFile().load();
        LoadResponseMessage loadResponseMessage = new LoadResponseMessage(getFile().getQueue(), this, getFile().getData());
        loadMessage.reply(loadResponseMessage);

        return this;
    }

    public State processStopMessage (StopMessage stopMessage) {
        if (getFile().isDirty())
            getFile().getWriter().sendWrite(getFile().getQueue(), this, getFile().getFilename(), getFile().getBytes());

        SingleFileStoppingState singleFileStoppingState = new SingleFileStoppingState(getFile());
        return singleFileStoppingState;
    }

    public State processAddSubscriberMessage (AddSubscriberMessage addSubscriberMessage) {
        getFile().addSubscriber(addSubscriberMessage.getSender(), addSubscriberMessage.getNotification());

        return getFile().getCurrentState();
    }

    public State processRemoveSubscriberMessage (RemoveSubscriberMessage removeSubscriberMessage) {
        getFile().removeSubscriber(removeSubscriberMessage.getSender());

        return getFile().getCurrentState();
    }
}
