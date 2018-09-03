package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Abort the current auction
 */
public class AuctionAbortMessage extends Message {
    public AuctionAbortMessage (BlockingQueue<Message> queue, Object senderObject) {
        super(Subjects.AuctionAbort, queue, senderObject);
    }
}
