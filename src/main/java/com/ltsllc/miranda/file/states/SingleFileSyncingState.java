package com.ltsllc.miranda.file.states;

import com.google.gson.Gson;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.messages.GetFileResponseMessage;
import com.ltsllc.miranda.node.messages.GetFileMessage;

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
        List list = ourGson.fromJson(getFileResponseMessage.getContents(), getListType());
        getFile().merge(list);

        return this;
    }

    private State processGetFileMessage (GetFileMessage getFileMessage) {
        GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(getFile().getQueue(), this, getName(),
                getFile().getBytes());

        getFileMessage.reply(getFileResponseMessage);

        return this;
    }
}
