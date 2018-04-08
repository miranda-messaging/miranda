package com.ltsllc.miranda.remoteNode.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.node.networkMessages.GetVersionsWireMessage;
import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.remoteNode.actions.GetVersionsAction;

/**
 * A State that waits for a certificate
 */
public class RemoteNodeStartState extends State {

    /**
     * Process a message. We only care about network messages --- let the superclass handle all the rest.
     *
     * @param m The message to process.
     * @return The next state.
     * @throws MirandaException If the superclass encounters a problem processing the message.
     */
    @Override
    public State processMessage(Message m) throws MirandaException {
        State nextState = this;

        switch (m.getSubject()) {
            case NetworkMessage:
            {
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

    /**
     * Process a network message --- the only thing we care about is a request to join the cluster.
     *
     * @param networkMessage The nework message.
     * @return The next state.
     */
    public State processNetworkMessage(NetworkMessage networkMessage) {
        State nextState = this;

        switch (networkMessage.getWireMessage().getWireSubject()) {
            case GetVersions: {
                GetVersionsWireMessage getVersionsWireMessage = (GetVersionsWireMessage) networkMessage.getWireMessage();
                // nextState = processGetVersionsMessage(getVersionsWireMessage);
                break;
            }

            default:
                nextState = super.processNetworkMessage(networkMessage);
                break;
        }

        return nextState;
    }

    /*
    public State processGetVersionsMessage(GetVersionsWireMessage getVersionsWireMessage) {
        GetVersionsAction getVersionsAction = new GetVersionsAction (getRemoteNode());
        getVersionsAction.start();
        return this;
    }
    */

}
