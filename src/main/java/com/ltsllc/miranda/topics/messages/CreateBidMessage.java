package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * A request to create a "bid" --- A listing with a random value long for each subscription
 */
public class CreateBidMessage extends Message {
    public CreateBidMessage (BlockingQueue<Message> queue, Object senderObject) {
        super (Subjects.CreateBid,queue, senderObject);
    }
}
