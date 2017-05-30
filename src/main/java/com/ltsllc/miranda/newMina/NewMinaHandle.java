package com.ltsllc.miranda.newMina;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
import com.sun.xml.internal.stream.util.BufferAllocator;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import java.util.concurrent.BlockingQueue;

/**
 * Created by clarkhobbie on 5/30/17.
 */
public class NewMinaHandle extends Handle {
    private static BufferAllocator bufferAllocator = new BufferAllocator();

    private IoSession ioSession;

    public IoSession getIoSession() {
        return ioSession;
    }

    public static BufferAllocator getBufferAllocator() {
        return bufferAllocator;
    }

    public NewMinaHandle (IoSession ioSession, BlockingQueue<Message> queue) {
        super(queue);
        this.ioSession = ioSession;
    }

    public void send (SendNetworkMessage sendNetworkMessage) throws NetworkException {
        char[] jsonArray = sendNetworkMessage.toJson().toCharArray();
        char[] buffer = getBufferAllocator().getCharBuffer (jsonArray.length);
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = jsonArray[i];
        }
    }

    public void close () {
        ioSession.closeNow();
    }

    public void panic () {
        close();
    }

}
