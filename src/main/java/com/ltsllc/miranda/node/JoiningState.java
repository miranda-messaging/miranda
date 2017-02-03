package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 1/29/2017.
 */
public class JoiningState extends NodeState {
    private Logger logger = Logger.getLogger(JoiningState.class);

    private Node node;

    public Node getNode() {
        return node;
    }

    public JoiningState(Node  n) {
        super(n);

        this.node = n;
    }

    public State start ()
    {
        JoinMessage joinWireMessage = new JoinMessage(getNode().getQueue());
        sendOnWire(joinWireMessage);

        return this;
    }

    public State processMessage (Message m) {
        State nextState = this;

        switch (m.getSubject()) {
            case NetworkMessage: {
                NetworkMessage networkMessage = (NetworkMessage) m;
                nextState = processNetworkMessage(networkMessage);
                break;
            }

            default: {
                nextState = super.processMessage(m);
                break;
            }

        }

        return nextState;
    }

    private State processNetworkMessage (NetworkMessage networkMessage) {
        State nextState = this;

        switch (networkMessage.getSubject()) {
            case JoinSuccess: {
                JoinSuccessMessage joinSuccessMessage = (JoinSuccessMessage) networkMessage;
                nextState = processJoinSuccess(joinSuccessMessage);
                break;
            }

        }

        return nextState;
    }


    private State processJoinSuccess (JoinSuccessMessage joinSucessMessage) {
        State nextState = new ReadyState(getNode());

        return nextState;
    }
}
