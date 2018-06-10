package com.ltsllc.miranda.operations.bootstrap;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.operations.Operation;
import com.ltsllc.miranda.operations.bootstrap.states.Start;

import java.util.concurrent.BlockingQueue;

/**
 * This class is created to keep track of Miranda's attempt to bootstrap itself
 */
public class BootstrapOperation extends Operation {
    public BootstrapOperation (BlockingQueue<Message> requester) {
        super ("bootstrap operation", requester);
    }

    public BootstrapOperation () {
        super ("bootstrap operation", null);
        setCurrentState(new Start(this));
    }


}
