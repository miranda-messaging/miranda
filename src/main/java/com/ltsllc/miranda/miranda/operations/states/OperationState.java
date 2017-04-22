package com.ltsllc.miranda.miranda.operations.states;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/17/2017.
 */
public class OperationState extends State {
    private BlockingQueue<Message> requester;

    public BlockingQueue<Message> getRequester() {
        return requester;
    }

    public OperationState (Consumer consumer, BlockingQueue<Message> requester) {
        super(consumer);

        this.requester = requester;
    }
}
