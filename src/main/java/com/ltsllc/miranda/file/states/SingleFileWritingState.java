package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.messages.WriteResponseMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.shutdown.ShutdownMessage;
import com.ltsllc.miranda.writer.WriteFailedMessage;
import com.ltsllc.miranda.writer.WriteSucceededMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class SingleFileWritingState extends State {
    private List<BlockingQueue<Message>> writeListeners = new ArrayList<>();

    public List<BlockingQueue<Message>> getWriteListeners() {
        return writeListeners;
    }

    public void setWriteListeners(List<BlockingQueue<Message>> writeListeners) {
        this.writeListeners = writeListeners;
    }

    public SingleFileWritingState(SingleFile singleFile) {
        super(singleFile);
    }

    public void addWriteListener (BlockingQueue<Message> listener) {
        writeListeners.add(listener);
    }

    public void tellWriteListeners () {
        try {
            WriteResponseMessage writeResponseMessage = new WriteResponseMessage(getFile().getQueue(), this);
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
            case WriteSucceeded: {
                WriteSucceededMessage writeSucceededMessage = (WriteSucceededMessage) message;
                nextState = processWriteSucceededMessage(writeSucceededMessage);
                break;
            }

            case WriteFailed: {
                WriteFailedMessage writeFailedMessage = (WriteFailedMessage) message;
                nextState = processWriteFailedMessage(writeFailedMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processWriteSucceededMessage (WriteSucceededMessage writeSucceededMessage) {
        try {
            return new SingleFileReadyState(getFile());
        } catch (MirandaException e) {
            Panic panic = new Panic("Exception creating ready state", e, Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
            return this;
        }
    }

    public State processWriteFailedMessage (WriteFailedMessage writeFailedMessage) {
        Panic panic = new Panic("Write failed", writeFailedMessage.getCause(),
                Panic.Reasons.ExceptionWritingFile);
        Miranda.panicMiranda(panic);
        return this;
    }

    public SingleFile getFile() {
        return (SingleFile) getContainer();
    }
}
