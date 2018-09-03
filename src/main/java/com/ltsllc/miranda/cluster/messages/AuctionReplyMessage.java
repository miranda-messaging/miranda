package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.results.ResultObject;
import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

import static com.ltsllc.miranda.message.Message.Subjects.AuctionReply;

public class AuctionReplyMessage extends Message {

    public AuctionReplyMessage (Results result, BlockingQueue<Message> queue, Object sender) {
        super (AuctionReply, queue, sender);
    }
}
