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

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.MirandaFactory;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.network.messages.ConnectToMessage;
import com.ltsllc.miranda.util.Utils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by Clark on 3/3/2017.
 */
public class NettyNetwork extends Network {
    public static class LocalListener implements GenericFutureListener<ChannelFuture> {
        private SslContext sslContext;

        public SslContext getSslContext() {
            return sslContext;
        }

        public void operationComplete (ChannelFuture channelFuture) {

        }
    }
    private Logger logger = Logger.getLogger(NettyNetwork.class);

    private Bootstrap bootstrap;

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public NettyNetwork (MirandaFactory factory) throws MirandaException {
        try {
            SslContext sslContext = factory.buildNettyClientSslContext();
            NewConnectionInitializer newConnectionInitializer = new NewConnectionInitializer(sslContext);
            this.bootstrap = Utils.createClientBootstrap(newConnectionInitializer);
        } catch (GeneralSecurityException | IOException e) {
            throw new MirandaException("Exception trying to create SSL context", e);
        }
    }

    public Handle basicConnectTo (ConnectToMessage connectToMessage) throws NetworkException
    {
        getBootstrap().connect(connectToMessage.getHost(), connectToMessage.getPort());

        return null;
    }


    public Handle createHandle (Object o) {
        throw new IllegalStateException("not implemented");
    }
}
