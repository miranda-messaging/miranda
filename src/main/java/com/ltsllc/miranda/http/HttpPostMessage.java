package com.ltsllc.miranda.http;

import com.ltsllc.miranda.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/18/2017.
 */
public class HttpPostMessage extends Message {
    private HttpRequest request;
    private String content;
    private ChannelHandlerContext channelHandlerContext;


    public HttpPostMessage (BlockingQueue<Message> senderQueue, Object sender, HttpRequest request, String content, ChannelHandlerContext ctx)
    {
        super(Subjects.HttpPost, senderQueue, sender);

        this.request = request;
        this.content = content;
        this.channelHandlerContext = ctx;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public String getContent() {
        return content;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
