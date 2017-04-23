package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.subsciptions.Subscription;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 */
public class GetSubscriptionResponseMessage extends Message {
    private Results result;
    private Subscription subscription;

    public Subscription getSubscription() {
        return subscription;
    }

    public Results getResult() {
        return result;
    }

    public GetSubscriptionResponseMessage(BlockingQueue<Message> sender, Object senderObject, Results result,
                                          Subscription subscription) {
        super(Subjects.GetSubscriptionResponse, sender, senderObject);
        this.result = result;
        this.subscription = subscription;
    }
}
