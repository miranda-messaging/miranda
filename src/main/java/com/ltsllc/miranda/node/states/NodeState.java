package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.messages.SendMessageMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 1/29/2017.
 */
public class NodeState extends State {
    private Logger logger = Logger.getLogger(NodeState.class);

    private Network network;

    public Node getNode() {
        return (Node) getContainer();
    }

    public Network getNetwork() {
        return network;
    }

    public NodeState (Node node, Network network) {
        super(node);

        this.network = network;
    }

    public void sendOnWire (WireMessage wireMessage) {
        String json = wireMessage.getJson() + '\n';
        logger.info("Sending " + json);
        SendMessageMessage message = new SendMessageMessage(getNode().getQueue(), this, getNode().getHandle(),json);
        send(getNode().getNetwork().getQueue(), message);
    }

    public State processNetworkMessage (NetworkMessage networkMessage) {
        logger.fatal (this + " does not understand network message " + networkMessage.getWireMessage().getWireSubject());
        logger.fatal ("message created at", networkMessage.getWhere());
        System.exit(1);

        return this;
    }

    public void sendOnNetwork (WireMessage wireMessage) {
        getNetwork().sendMessage(getNode().getQueue(), this, getNode().getHandle(), wireMessage);
    }
}
