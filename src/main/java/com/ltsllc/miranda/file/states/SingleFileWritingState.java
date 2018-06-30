package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.writer.WriteResponseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class SingleFileWritingState extends State {
    private List<BlockingQueue<Message>> writeListeners = new ArrayList<>();
    private State readyState;

    public State getReadyState() {
        return readyState;
    }

    public void setReadyState(State readyState) {
        this.readyState = readyState;
    }

    public List<BlockingQueue<Message>> getWriteListeners() {
        return writeListeners;
    }

    public void setWriteListeners(List<BlockingQueue<Message>> writeListeners) {
        this.writeListeners = writeListeners;
    }

    public SingleFileWritingState(SingleFile singleFile, State readyState) {
        super(singleFile);
        setReadyState(readyState);
    }

    public void addWriteListener (BlockingQueue<Message> listener) {
        writeListeners.add(listener);
    }

    public void tellWriteListeners (WriteResponseMessage writeResponseMessage) {
        try {
            for (BlockingQueue<Message> listener : getWriteListeners()) {
                listener.put(writeResponseMessage);
            }
        } catch (InterruptedException e) {
            Panic panic = new Panic("Exception trying to send message.", e, Panic.Reasons.ExceptionSendingMessage);
            Miranda.panicMiranda(panic);
        }
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getContainer().getCurrentState();

        switch (message.getSubject()) {
            case WriteResponse: {
                WriteResponseMessage writeResponseMessage = (WriteResponseMessage) message;
                nextState = processWriteResonseMessage(writeResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processWriteResonseMessage (WriteResponseMessage writeResponseMessage) {
        return getReadyState();
    }

    public SingleFile getFile() {
        return (SingleFile) getContainer();
    }
}
