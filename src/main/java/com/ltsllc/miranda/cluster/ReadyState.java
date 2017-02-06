package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;

/**
 * Created by Clark on 1/3/2017.
 */
public class ReadyState extends State {
    public ReadyState(Consumer container) {
        super(container);
    }

    @Override
    public State processMessage(Message m) {
        return super.processMessage(m);
    }
}
