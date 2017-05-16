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

package com.ltsllc.miranda.netty;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;

/**
 * A handle for use with the netty library
 */
public class NettyHandle extends Handle {
    private static Gson ourGson = new Gson();

    private Channel channel;


    public NettyHandle(BlockingQueue<Message> notify, Channel channel) {
        super(notify);

        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void send (SendNetworkMessage sendNetworkMessage) {
        String json = ourGson.toJson(sendNetworkMessage.getWireMessage());
        ByteBuf byteBuf = Unpooled.directBuffer(json.length());
        ByteBufUtil.writeUtf8(byteBuf, json);
        getChannel().writeAndFlush(byteBuf);
    }


    public void close () {
        getChannel().close();
    }


    public void panic () {
        getChannel().close();
    }

}
