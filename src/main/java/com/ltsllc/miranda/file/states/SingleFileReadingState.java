package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.messages.CreateMessage;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.reader.ReadResponseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class SingleFileReadingState extends State {
    private List<BlockingQueue<Message>> loaderListeners = new ArrayList<>();
    private State readyState;

    public State getReadyState() {
        return readyState;
    }

    public void setReadyState(State readyState) {
        this.readyState = readyState;
    }

    public void addLoaderListener(BlockingQueue<Message> listener) {
        loaderListeners.add(listener);
    }

    public List<BlockingQueue<Message>> getLoaderListeners() {
        return loaderListeners;
    }

    public SingleFile getFile() {
        return (SingleFile) getContainer();
    }

    public SingleFileReadingState(SingleFile singleFile, State readyState) throws MirandaException {
        super(singleFile);
        setReadyState(readyState);
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

            case FileLoaded: {
                FileLoadedMessage fileLoadedMessage = (FileLoadedMessage) message;
                nextState = processFileLoadedMessage (fileLoadedMessage);
                break;
            }

            case Create: {
                CreateMessage createMessage = (CreateMessage) message;
                nextState = processCreateMessage(createMessage);
                break;
            }

            case GarbageCollection: {
                defer(message);
                break;
            }

            case List: {
                defer(message);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    public void addAListener(Message message) {
        addLoaderListener(message.getSender());
    }

    public State processReadResponseMessage(ReadResponseMessage readResponseMessage) throws MirandaException {
        if (readResponseMessage.getResult() == Results.Success) {
            restoreDeferredMessages();
            getFile().setData(readResponseMessage.getData());
            getFile().fireFileLoaded();
            return getReadyState();
        } else if (readResponseMessage.getResult() == Results.FileDoesNotExist) {
            getFile().fireFileDoesNotExist();
            return getReadyState();
        } else {
            ReadResponseMessage readResponseMessage2 = new ReadResponseMessage(readResponseMessage.getResult(),
                    getFile().getQueue(), getFile());
            getFile().fireMessage (readResponseMessage2);
            return this;
        }
    }

    public State processFileLoadedMessage(FileLoadedMessage fileLoadedMessage) {
        List list = (List) fileLoadedMessage.getData();
        getFile().setData(list);
        return getReadyState();
    }

    public State processCreateMessage(CreateMessage createMessage) {
        getFile().getWriter().sendWrite(getFile().getQueue(), this, getFile().getFilename(), getFile().getBytes());
        return new SingleFileWritingState(getFile(), getReadyState());
    }
}
