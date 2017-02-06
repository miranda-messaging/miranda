package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.ConnectMessage;
import com.ltsllc.miranda.network.ConnectToMessage;

import java.awt.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/6/2017.
 */
public class NodeStartState extends State {
    private Node node;
    private BlockingQueue<Message> network;


    public NodeStartState (Node node, BlockingQueue<Message> network) {
        super(node);
        this.node = node;
        this.network = network;
    }


    public BlockingQueue<Message> getNetwork() {
        return network;
    }

    public Node getNode() {

        return node;
    }

    public State processMessage (Message m) {
        State nextState = null;

        switch (m.getSubject()) {
            case Connect: {
                ConnectMessage connectMessage = (ConnectMessage) m;
                nextState = processConnectMessage(connectMessage);
                break;
            }

            default :
                nextState = super.processMessage(m);
                break;
        }

        return nextState;
    }


    private State processConnectMessage (ConnectMessage connectMessage) {
        ConnectToMessage connectToMessage = new ConnectToMessage(getNode().getDns(), getNode().getPort(), getNode().getQueue(), this);
        send(getNetwork(), connectToMessage);
        ConnectingState connectingState = new ConnectingState(getNode());
        return connectingState;
    }
}
