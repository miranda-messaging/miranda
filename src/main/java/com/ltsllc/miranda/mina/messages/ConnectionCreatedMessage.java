package com.ltsllc.miranda.mina.messages;

import com.ltsllc.miranda.message.Message;
import org.apache.mina.core.session.IoSession;

import java.util.concurrent.BlockingQueue;

public class ConnectionCreatedMessage extends Message {
    private IoSession ioSession;

    public IoSession getIoSession() {
        return ioSession;
    }

    public void setIoSession(IoSession ioSession) {
        this.ioSession = ioSession;
    }

    public ConnectionCreatedMessage (BlockingQueue<Message> senderQueue, Object senderObject, IoSession ioSession) {
        super(Subjects.ConnectionCreated, senderQueue, senderObject);
        setIoSession(ioSession);
    }
}
