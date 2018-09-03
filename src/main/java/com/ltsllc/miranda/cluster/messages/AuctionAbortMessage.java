package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Abort the current auction
 */
public class AuctionAbortMessage extends Message implements Cloneable {
    public AuctionAbortMessage (BlockingQueue<Message> queue, Object senderObject) {
        super(Subjects.AuctionAbort, queue, senderObject);
    }

    public AuctionAbortMessage () {}

    public Object clone () {
        AuctionAbortMessage clone = new AuctionAbortMessage();
        deepCopy (clone, this);
        return clone;
    }


}
