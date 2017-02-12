package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Utils;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/11/2017.
 */
public class GetFileResponseMessage extends Message {
    private String contents;
    private String requester;

    public GetFileResponseMessage (BlockingQueue<Message> senderQueue, Object sender, String requester, byte[] contents) {
        super(Subjects.GetFileResponse, senderQueue, sender);

        String hexString = Utils.bytesToString(contents);

        this.requester = requester;
        this.contents = hexString;
    }

    public GetFileResponseMessage (BlockingQueue<Message> senderQueue, Object sender, String requester, String contents) {
        super(Subjects.GetFileResponse, senderQueue, sender);

        this.requester = requester;
        this.contents = contents;
    }

    public GetFileResponseMessage (BlockingQueue<Message> senderQueue, Object sender, String requester) {
        super(Subjects.GetFileResponse, senderQueue, sender);

        this.requester = requester;
    }


    public String getContents() {
        return contents;
    }

    public String getRequester() {
        return requester;
    }
}
