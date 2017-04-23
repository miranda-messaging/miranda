package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class DeleteSubscriptionResponseMessage extends Message {
    private Results result;

    public Results getResult() {
        return result;
    }

    public DeleteSubscriptionResponseMessage(BlockingQueue<Message> senderQueue, Object sender, Results result) {
        super(Subjects.DeleteSubscriptionResponse, senderQueue, sender);

        this.result = result;
    }
}
