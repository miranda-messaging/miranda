package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
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
        String s = wireMessage.getJson();
        ByteBuf byteBuf = Unpooled.directBuffer(s.length());
        ByteBufUtil.writeUtf8(byteBuf, s);
        getNode().getChannel().writeAndFlush(byteBuf);
    }
}
