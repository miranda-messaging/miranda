package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.node.Node;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLEngine;


/**
 * Created by Clark on 1/21/2017.
 */
public class NodeChannelInitializer extends ChannelInitializer<SocketChannel> {
    private SslContext sslContext;

    public NodeChannelInitializer (SslContext sslContext) {
        this.sslContext = sslContext;
    }

    public void initChannel (SocketChannel sc) {
        sc.pipeline().addLast(sslContext.newHandler(sc.alloc()));
        Node n = new Node(sc.remoteAddress());
        n.start();
    }
}
