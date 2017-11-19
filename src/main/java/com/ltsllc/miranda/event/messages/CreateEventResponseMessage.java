package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.results.Results;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/7/2017.
 */
public class CreateEventResponseMessage extends Message {
    private Results result;
    private String guid;

    public String getGuid() {
        return guid;
    }

    public Results getResult() {
        return result;
    }

    public CreateEventResponseMessage(BlockingQueue<Message> senderQueue, Object sender, Results result, String guid) {
        super(Subjects.CreateResponse, senderQueue, sender);

        this.guid = guid;
        this.result = result;
    }


}
