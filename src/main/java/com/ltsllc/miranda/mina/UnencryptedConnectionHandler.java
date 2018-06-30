package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.messages.NewConnectionMessage;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.util.concurrent.BlockingQueue;

public class UnencryptedConnectionHandler extends IoHandlerAdapter {
    private Network network;
    private BlockingQueue<Message> listener;

    public BlockingQueue<Message> getListener() {
        return listener;
    }

    public void setListener(BlockingQueue<Message> listener) {
        this.listener = listener;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);

        Handle handle = getNetwork().createHandle(session);
        NewConnectionMessage newConnectionMessage = new NewConnectionMessage(null, this, -1);

        getListener().put(newConnectionMessage);
    }
}
