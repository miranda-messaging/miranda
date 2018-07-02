/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.http.messages;

import com.ltsllc.miranda.message.Message;
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


    public HttpPostMessage(BlockingQueue<Message> senderQueue, Object sender, HttpRequest request, String content, ChannelHandlerContext ctx) {
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
