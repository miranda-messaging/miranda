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
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.util.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/3/2017.
 */
public class NettyChannelHandler extends ChannelInboundHandlerAdapter {
    private static Gson ourGson = new Gson();
    private static Logger logger = Logger.getLogger(NettyChannelHandler.class);

    private BlockingQueue<Message> notify;

    public BlockingQueue<Message> getNotify() {
        return notify;
    }

    public NettyChannelHandler (BlockingQueue<Message> notify) {
        this.notify = notify;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] buffer = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, buffer);
            String s = new String(buffer);
            String[] fields = s.split("\n");
            for (String json : fields) {
                WireMessage pass1 = ourGson.fromJson(json, WireMessage.class);
                Type type = getClass().forName(pass1.getClassName());
                WireMessage wireMessage = ourGson.fromJson(json, type);
                NetworkMessage networkMessage = new NetworkMessage(null, this, wireMessage);
                getNotify().put(networkMessage);
            }
        } catch (Exception e) {
            Utils.closeIgnoreExceptions(ctx);
            String message = "Exception receiving message";
            logger.error (message, e);
            Panic panic = new Panic(message, e, Panic.Reasons.ExceptionReceivingMessage);
            Network.getInstance().panic(panic);
        }
    }
}
