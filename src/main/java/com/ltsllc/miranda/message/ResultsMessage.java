package com.ltsllc.miranda.message;

import com.ltsllc.miranda.Results;

import java.util.concurrent.BlockingQueue;

/**
 * A message that consists of a single result,
 */
public class ResultsMessage extends Message{
    private Results result;

    public Results getResult() {
        return result;
    }

    public void setResult(Results result) {
        this.result = result;
    }

    public ResultsMessage (Results result, BlockingQueue<Message> queue, Object sender) {
        super(Subjects.Result, queue, sender);
        setResult(result);
    }
}
