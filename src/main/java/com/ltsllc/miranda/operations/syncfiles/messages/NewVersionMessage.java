package com.ltsllc.miranda.operations.syncfiles.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

public class NewVersionMessage extends Message {
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public NewVersionMessage (BlockingQueue senderQueue, Object sender, byte[] data) {
        super(Subjects.NewVersion, senderQueue, sender);
        setData(data);
    }
}
