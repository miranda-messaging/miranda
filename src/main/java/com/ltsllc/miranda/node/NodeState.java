package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 1/29/2017.
 */
public class NodeState extends State {
    private Logger logger = Logger.getLogger(NodeState.class);

    private Node node;

    public Node getNode() {
        return node;
    }

    public NodeState (Node n) {
        super(n);
        this.node = n;
    }

    public void sendOnWire (WireMessage wireMessage) {
        String s = wireMessage.getJson() + '\n';
        logger.info("Sending " + s);
        ByteBuf byteBuf = Unpooled.directBuffer(1 + s.length());
        ByteBufUtil.writeUtf8(byteBuf, s);
        try {
            getNode().getChannel().writeAndFlush(byteBuf).sync();
        } catch (InterruptedException e) {
            logger.fatal("Interrupted while trying to send message", e);
            System.exit(1);
        }
    }


    public State processNetworkMessage (NetworkMessage networkMessage) {
        logger.error (this + " does not understand " + networkMessage.getWireMessage().getWireSubject());
        return this;
    }
}
