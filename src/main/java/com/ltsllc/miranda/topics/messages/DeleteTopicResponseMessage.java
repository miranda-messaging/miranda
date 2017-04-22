package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/10/2017.
 */
public class DeleteTopicResponseMessage extends Message {
    private Results result;
    private String additionalInfo;

    public Results getResult () {
        return result;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public DeleteTopicResponseMessage(BlockingQueue<Message> senderQueue, Object sender, Results result) {
        super(Subjects.DeleteTopicResponse, senderQueue, sender);

        this.result = result;
    }

    public DeleteTopicResponseMessage(BlockingQueue<Message> senderQueue, Object sender, Results result,
                                      String additionalInfo) {
        super(Subjects.DeleteTopicResponse, senderQueue, sender);

        this.result = result;
        this.additionalInfo = additionalInfo;
    }

}
