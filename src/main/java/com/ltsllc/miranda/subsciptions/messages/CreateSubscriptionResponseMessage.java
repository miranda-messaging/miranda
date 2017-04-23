package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.subsciptions.Subscription;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class CreateSubscriptionResponseMessage extends Message {
    private Results result;

    public Results getResult() {
        return result;
    }

    public CreateSubscriptionResponseMessage(BlockingQueue<Message> senderQueue, Object sender, Results result) {
        super (Subjects.CreateSubscriptionResponse, senderQueue, sender);

        this.result = result;
    }
}
