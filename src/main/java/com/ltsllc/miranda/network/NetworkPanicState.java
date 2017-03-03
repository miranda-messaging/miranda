package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.PanicMessage;
import com.ltsllc.miranda.State;

/**
 * Created by Clark on 3/2/2017.
 */
public class NetworkPanicState extends State {
    public Network getNetwork () {
        return (Network) getContainer();
    }

    public NetworkPanicState (Network network) {
        super(network);
    }

    public State start () {
        for (Handle handle : getNetwork().getHandleMap().values()) {
            PanicMessage panicMessage = new PanicMessage(getNetwork().getQueue(), this);
            try {
                handle.getQueue().put(panicMessage);
            } catch (InterruptedException e) {
                //
                // ignore the exception and keep going
                //
            }
        }

        return this;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case Disconnect: {
                CloseMessage disconnectMessage = (CloseMessage) message;
                nextState = processDisconnectMessage(disconnectMessage);
                break;
            }

            default: {
                nextState = processOtherMessage(message);
                break;
            }
        }

        return nextState;
    }


    private State processDisconnectMessage (CloseMessage disconnectMessage) {
        getNetwork().forceDisconnect(disconnectMessage.getHandle());

        return this;
    }

    private State processOtherMessage (Message message) {
        PanicMessage panicMessage = new PanicMessage(getNetwork().getQueue(), this);
        message.reply(panicMessage);

        return this;
    }
}
