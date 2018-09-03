package com.ltsllc.miranda.operations.auction;

import com.ltsllc.miranda.message.Message;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class BidMessage extends Message {
    private Map<String, Long> bid;

    public Map<String, Long> getBid() {
        return bid;
    }

    public void setBid(Map<String, Long> bid) {
        this.bid = bid;
    }

    public BidMessage (BlockingQueue<Message> queue, Object senderObject, Map<String, Long> bid) {
        super(Message.Subjects.Bid, queue, senderObject);
        setBid(bid);
    }
}
