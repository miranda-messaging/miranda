package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.messages.WriteResponseMessage;

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
            M
        }
    }

    public SingleFile getFile() {
        return (SingleFile) getContainer();
    }
}
