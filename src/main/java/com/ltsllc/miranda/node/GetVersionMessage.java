package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/6/2017.
 */
public class GetVersionMessage extends Message {
    private BlockingQueue<Message> requester;

    public GetVersionMessage (BlockingQueue<Message> queue, Object sender, BlockingQueue<Message> requester) {
        super(Subjects.GetVersion, queue, sender);

        this.requester = requester;
    }

    public BlockingQueue<Message> getRequester() {
        return requester;
    }
}
