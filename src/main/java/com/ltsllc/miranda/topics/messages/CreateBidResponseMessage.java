package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.operations.auction.Bid;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * A response to a CreateBidMessage
 */
public class CreateBidResponseMessage extends Message {
    private Bid bid;

    public Bid getBid() {
        return bid;
    }

    public void setBid(Bid bid) {
        this.bid = bid;
    }

    public CreateBidResponseMessage (Bid bid, BlockingQueue<Message> queue, Object senderObject) {
        super(Subjects.CreateBidResponse, queue, senderObject);
        setBid(bid);
    }
}
