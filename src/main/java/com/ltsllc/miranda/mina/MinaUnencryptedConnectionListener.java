package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.mina.messages.ConnectionCreatedMessage;
import com.ltsllc.miranda.mina.states.MinaUnecryptedConnectionListenerReadyState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.UnencryptedConnectionListener;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;

public class MinaUnencryptedConnectionListener extends UnencryptedConnectionListener {

    public static class ConnectionHandler extends IoHandlerAdapter {
        private BlockingQueue<Message> listener;

        public ConnectionHandler (BlockingQueue<Message> listener) {
            this.listener = listener;
        }

        @Override
        public void sessionCreated(IoSession session) throws Exception {
            super.sessionCreated(session);

            ConnectionCreatedMessage connectionCreatedMessage = new ConnectionCreatedMessage(null, this,
                    session);

            listener.put(connectionCreatedMessage);
        }
    }

    public static class ConnectionThread implements Runnable {
        private BlockingQueue<Message> listener;
        private int port;

        public ConnectionThread (BlockingQueue<Message> queue) {
            listener = queue;
        }

        public void run () {
            try {
                ConnectionHandler connectionHandler = new ConnectionHandler(listener);
                IoAcceptor ioAcceptor = new NioSocketAcceptor();
                ioAcceptor.setHandler(connectionHandler);
                SocketAddress socketAddress = new InetSocketAddress(port);
                ioAcceptor.bind(socketAddress);
                try {
                    wait();
                } catch (InterruptedException e) {
                }
                ioAcceptor.dispose(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private Exception exception;


    @Override
    public void stopListening() {
        getConnectionThread().interrupt();
    }

    @Override
    public void startListening() {
        ConnectionThread connectionThread = new ConnectionThread(getQueue());
        Thread thread = new Thread(connectionThread);
        setConnectionThread(thread);
        getConnectionThread().start();
    }

    public MinaUnencryptedConnectionListener (Network network, Cluster cluster, int port) throws MirandaException {
        super(network, cluster, port);
        exception = new Exception();


        setCurrentState(new MinaUnecryptedConnectionListenerReadyState());
    }

}
