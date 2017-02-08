package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/6/2017.
 */
public class GetVersionMessage extends Message {
    public GetVersionMessage (BlockingQueue<Message> queue, Object sender) {
        super(Subjects.GetVersion, queue, sender);
    }
}
