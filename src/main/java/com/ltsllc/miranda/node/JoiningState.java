package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
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


    public State processNetworkMessage (NetworkMessage networkMessage) {
        State nextState = this;

        switch (networkMessage.getWireMessage().getWireSubject()) {
            case JoinSuccess:{
                logger.info ("got JoinSucess");
                nextState = new ReadyState(getNode());
                break;
            }

            default:
                nextState = super.processNetworkMessage(networkMessage);
                break;
        }

        return nextState;
    }


    private State processJoinSuccess (JoinSuccessMessage joinSucessMessage) {
        State nextState = new ReadyState(getNode());



        return nextState;
    }
}
