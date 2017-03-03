package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.SendMessageMessage;
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
        String json = wireMessage.getJson() + '\n';
        logger.info("Sending " + json);
        SendMessageMessage message = new SendMessageMessage(getNode().getQueue(), this, getNode().getHandle(),json);
        send(getNode().getNetwork(), message);
    }

    public State processNetworkMessage (NetworkMessage networkMessage) {
        logger.fatal (this + " does not understand network message " + networkMessage.getWireMessage().getWireSubject());
        logger.fatal ("message created at", networkMessage.getWhere());
        System.exit(1);

        return this;
    }
}
