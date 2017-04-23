package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class AuctionMessage extends Message {
    public AuctionMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.Auction, senderQueue, sender);
    }
}
