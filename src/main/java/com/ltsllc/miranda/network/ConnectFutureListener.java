package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.ConnectFailedMessage;
import com.ltsllc.miranda.node.ConnectedMessage;
import com.ltsllc.miranda.node.Node;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/21/2017.
 */


public class ConnectFutureListener implements FutureListener<Void> {

    private static Logger logger = Logger.getLogger(ConnectFutureListener.class);

    private BlockingQueue<Message> sender;
    private SslContext sslContext;

    public ConnectFutureListener(BlockingQueue<Message> sender, SslContext sslContext) {
        this.sender = sender;
        this.sslContext = sslContext;
    }

    private void gotConnection(Channel channel) {
        logger.info("got connection to " + channel.remoteAddress().toString());
        try {
            Node node = new Node((InetSocketAddress) channel.remoteAddress());
            channel.pipeline().addLast(sslContext.newHandler(channel.alloc()));
            NodeHandler nodeHandler = new NodeHandler(node);
            channel.pipeline().addLast(nodeHandler);
            ConnectedMessage connectedMessage = new ConnectedMessage(channel, sender);
            sender.put(connectedMessage);
        } catch (InterruptedException e) {
            logger.fatal("Interrupted while sending message", e);
            System.exit(1);
        }
    }

    private void connectFailed(Future<Void> future) {
        try {
            ConnectFailedMessage m = new ConnectFailedMessage(null, future.cause());
            sender.put(m);
        } catch (InterruptedException e) {
            logger.fatal ("Interrupted while trying to send message", e);
            System.exit(1);
        }
    }

    public void operationComplete(Future<Void> future) {
        ChannelFuture channelFuture = (ChannelFuture) future;
        if (future.isSuccess())
            gotConnection(channelFuture.channel());
        else
            connectFailed(future);
    }
}