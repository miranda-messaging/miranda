package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.holder.TopicHolder;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class CreateTopicResponseMessage extends Message {
    private Results result;

    public Results getResult() {
        return result;
    }

    public CreateTopicResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Results result) {
        super(Subjects.CreateTopicResponse, senderQueue, sender);

        this.result = result;
    }
}
