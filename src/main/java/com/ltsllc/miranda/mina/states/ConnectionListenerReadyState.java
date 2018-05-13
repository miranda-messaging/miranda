package com.ltsllc.miranda.mina.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.mina.ConnectionListener;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.messages.NewConnectionMessage;
import com.ltsllc.miranda.node.Node;

public class ConnectionListenerReadyState extends State {
    public ConnectionListener getConnectionListener () {
        return (ConnectionListener) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = null;

        switch (message.getSubject()) {
            case NewConnection: {
                NewConnectionMessage newConnectionMessage = (NewConnectionMessage) message;
                nextState = processNewConnectionMessage(newConnectionMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processNewConnectionMessage (NewConnectionMessage newConnectionMessage) {
        try {
            Node node = new Node(newConnectionMessage.getHandle(), getConnectionListener().getNetwork(),
                    getConnectionListener().getCluster());

            getConnectionListener().getCluster().sendNewNode(getConnectionListener().getQueue(), this, node);
        } catch (MirandaException e) {
            Panic panic = new Panic("Exception creating node",e,Panic.Reasons.ExceptionCreatingNode);
            Miranda.panicMiranda(panic);
        }

        return this;
    }
}
