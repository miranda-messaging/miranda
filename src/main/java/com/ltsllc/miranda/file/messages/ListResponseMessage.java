package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.message.Message;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * A response to a load message
 */
public class ListResponseMessage extends Message {
    private List list;
    private Results result;

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public Results getResult() {
        return result;
    }

    public void setResult(Results result) {
        this.result = result;
    }

    public ListResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Results result, List list) {
        super(Subjects.ListResponse, senderQueue, sender);
        setResult(result);
        setList(list);
    }
}
