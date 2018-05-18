package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.LoadResponseMessage;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.messages.CreateMessage;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.reader.ReadResponseMessage;
import com.ltsllc.miranda.writer.WriteFailedMessage;
import com.ltsllc.miranda.writer.WriteSucceededMessage;
import com.sun.net.httpserver.Authenticator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class SingleFileReadingState extends State {
    private List<BlockingQueue<Message>> loaderListeners = new ArrayList<>();

    public void addLoaderListener (BlockingQueue<Message> listener) {
        isteners.add(listener);
    }

    public List<BlockingQueue<Message>> getListeners() {
        return listeners;
    }

    public SingleFile getFile() {
        return (SingleFile) getContainer();
    }

    public SingleFileReadingState(SingleFile singleFile) throws MirandaException {
        super(singleFile);
    }


    public State processMessage(Message message) throws MirandaException {
        State nextState = getFile().getCurrentState();

        switch (message.getSubject()) {
            case Load: {
                addAListener(message);
                nextState = this;
                break;
            }

            case ReadResponse: {
                ReadResponseMessage readResponseMessage = (ReadResponseMessage) message;
                nextState = processReadResponseMessage(readResponseMessage);
                break;
            }

            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    public void addAListener (Message message) {
        addLoaderListener(message.getSender());
    }

    public State processReadResponseMessage(ReadResponseMessage readResponseMessage) {
        if (readResponseMessage.getResult() == ReadResponseMessage.Results.Success) {
            getFile().setData(readResponseMessage.getData());

            tellLoader
        }
    }
}
