package com.ltsllc.miranda.event.messages;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.Results;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/7/2017.
 */
public class CreateEventResponseMessage extends Message {
    private Results result;
    private String guid;

    public Results getResult() {
        return result;
    }

    public void setResult(Results result) {
        this.result = result;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public CreateEventResponseMessage(BlockingQueue<Message> senderQueue, Object sender, Results result, String guid) {
        super(Subjects.CreateEventResponse, senderQueue, sender);

        this.guid = guid;
        this.result = result;
    }

    public CreateEventResponseMessage (CreateEventResponseMessage other) {
        super(other);

        setGuid(other.getGuid());
        setResult(other.getResult());
    }
}
