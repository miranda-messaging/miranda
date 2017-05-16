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

import com.ltsllc.miranda.network.Network;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;


/**
 * Created by Clark on 3/3/2017.
 */
public class NewConnectionInitializer extends ChannelInitializer<SocketChannel> {
    private Logger logger = Logger.getLogger(NewConnectionInitializer.class);

    private SslContext sslContext;

    public SslContext getSslContext() {
        return sslContext;
    }

    public NewConnectionInitializer (SslContext sslContext) {
        this.sslContext = sslContext;
    }

    public void initChannel (SocketChannel socketChannel) {
        logger.info ("Got connection from " + socketChannel.remoteAddress());

        if (null != getSslContext()) {
            SslHandler sslHandler = getSslContext().newHandler(socketChannel.alloc());
            socketChannel.pipeline().addLast(sslHandler);
        }

        NettyHandle nettyHandle = new NettyHandle(Network.getInstance().getQueue(), socketChannel);
        Network.getInstance().newConnection(nettyHandle);
    }
}
