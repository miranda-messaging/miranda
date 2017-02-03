package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;

/**
 * Created by Clark on 1/30/2017.
 */
public class ServerConnectedState extends State {
    public ServerConnectedState (Consumer container) {
        super(container);
    }

    public State processMessage (Message m) {
        throw new IllegalStateException();
    }
}
