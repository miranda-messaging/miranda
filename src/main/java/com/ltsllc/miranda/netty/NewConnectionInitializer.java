package com.ltsllc.miranda.netty;

import com.ltsllc.miranda.network.Network;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;


/**
 * Created by Clark on 3/3/2017.
 */
public class NewConnectionInitializer extends ChannelInitializer<SocketChannel> {
    private Logger logger = Logger.getLogger(NewConnectionInitializer.class);

    private SslContext sslContext;

    public SslContext getSslContext() {
        return sslContext;
    }

    public NewConnectionInitializer (SslContext sslContext) {
        this.sslContext = sslContext;
    }

    public void initChannel (SocketChannel socketChannel) {
        logger.info ("Got connection from " + socketChannel.remoteAddress());

        if (null != getSslContext()) {
            SslHandler sslHandler = getSslContext().newHandler(socketChannel.alloc());
            socketChannel.pipeline().addLast(sslHandler);
        }

        NettyHandle nettyHandle = new NettyHandle(-1, Network.getInstance().getQueue(), socketChannel);
        Network.getInstance().newConnection(nettyHandle);
    }
}
