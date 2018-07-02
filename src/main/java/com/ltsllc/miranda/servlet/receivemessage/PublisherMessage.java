package com.ltsllc.miranda.servlet.receivemessage;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * A message that indicates a publisher has published a new message
 */
public class PublisherMessage extends Message {
    private byte[] content;
    private String url;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PublisherMessage(BlockingQueue<Message> senderQueue, Object sender, byte[] content, String url) {
        super(Subjects.PublisherMessage, senderQueue, sender);

        setContent(content);
        setUrl(url);
    }
}
