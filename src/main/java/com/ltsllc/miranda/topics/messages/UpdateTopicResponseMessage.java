package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/10/2017.
 */
public class UpdateTopicResponseMessage extends Message {
    private Results result;

    public Results getResult() {
        return result;
    }

    public UpdateTopicResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Results result) {
        super(Subjects.UpdateTopicResponse, senderQueue, sender);

        this.result = result;
    }
}
