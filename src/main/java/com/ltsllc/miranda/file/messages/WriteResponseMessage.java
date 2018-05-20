package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

public class WriteResponseMessage extends Message {
    public enum Results {
        Unknown,

        Success,
        Failure,
        Error
    }

    private Results result;
    private Throwable exception;

    public WriteResponseMessage (BlockingQueue<Message> queue, Object senderObject) {
        super(Subjects.WriteResponse, queue, senderObject);
    }
}
