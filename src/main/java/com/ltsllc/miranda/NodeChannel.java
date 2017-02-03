package com.ltsllc.miranda;

import com.ltsllc.miranda.cluster.NodeHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.apache.log4j.Logger;


/**
 * Created by Clark on 1/20/2017.
 */
public class NodeChannel extends ChannelInitializer<SocketChannel> {
    private Logger logger = Logger.getLogger(NodeChannel.class);

    public void initChannel (SocketChannel sc) {
        logger.info("got connection");
        sc.pipeline().addLast(new NodeHandler());
    }
}
