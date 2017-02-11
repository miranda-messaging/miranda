package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.NewConnectionMessage;
import com.ltsllc.miranda.node.GetVersionMessage;

/**
 * Created by Clark on 2/10/2017.
 */
public class ReadyState extends State {
    private Miranda miranda;

    public ReadyState (Miranda miranda) {
        super(miranda);

        this.miranda = miranda;
    }

    public Miranda getMiranda() {
        return miranda;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case NewConnection: {
                NewConnectionMessage newConnectionMessage = (NewConnectionMessage) message;
                nextState = processNewConnectionMessage(newConnectionMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }

    private State processNewConnectionMessage (NewConnectionMessage newConnectionMessage) {
        GetVersionMessage getVersionMessage = new GetVersionMessage(getMiranda().getQueue(), this);
        send(newConnectionMessage.getNode().getQueue(), getVersionMessage);
        return this;
    }
}
