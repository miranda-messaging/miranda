package com.ltsllc.miranda.netty;

/**
 * Created by Clark on 2/10/2017.
 */

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.server.HttpPostMessage;
import com.ltsllc.miranda.server.HttpServer;
import com.ltsllc.miranda.server.HttpServerHandler;
import com.ltsllc.miranda.util.Utils;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * The HTTP server for the system.
 */
public class NettyHttpServer extends HttpServer {
    private class LocalChannelInitializer extends ChannelInitializer<SocketChannel> {
        private SslContext sslContext;
        private NettyHttpServer server;

        public LocalChannelInitializer(SslContext sslContext, NettyHttpServer server) {
            this.sslContext = sslContext;
            this.server = server;
        }

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            if (null != sslContext) {
                SslHandler sslHandler = sslContext.newHandler(socketChannel.alloc());
                socketChannel.pipeline().addLast(sslHandler);
            }

            socketChannel.pipeline().addLast(new HttpServerCodec());

            HttpServerHandler httpServerHandler = new HttpServerHandler(server);
            socketChannel.pipeline().addLast(httpServerHandler);
        }
    }

    private Logger logger = Logger.getLogger(NettyHttpServer.class);

    private Map<String, BlockingQueue<Message>> postMap = new HashMap<String, BlockingQueue<Message>>();
    private int port;

    public NettyHttpServer(int port, SslContext sslContext) {
        this.port = port;
        this.sslContext = sslContext;
    }

    public void startup() {
        try {
            MirandaProperties properties = Miranda.properties;
            MirandaProperties.EncryptionModes mode = properties.getEncrptionModeProperty(MirandaProperties.PROPERTY_ENCRYPTION_MODE);

            SslContext sslContext = Miranda.factory.buildServerSslContext();

            LocalChannelInitializer localChannelInitializer = new LocalChannelInitializer(sslContext, this);
            ServerBootstrap serverBootstrap = Utils.createServerBootstrap(localChannelInitializer);

            serverBootstrap.bind(port);
        } catch (MirandaException e) {
            Panic panic = new StartupPanic("Exception trying to create SSL context", e, StartupPanic.StartupReasons.WebServer);
            Miranda.getInstance().panic(panic);
        }
    }

    private SslContext sslContext;

    public SslContext getSslContext() {
        return sslContext;
    }

    public void setSslContext(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    public void processGet(String url, HttpRequest request) {
        logger.info("GET " + url);
    }

    public void registerPostHandler(String path, BlockingQueue<Message> handlerQueue) {
        postMap.put(path, handlerQueue);
    }


    public void processPost(String path, HttpRequest request, String content, ChannelHandlerContext ctx) {
        BlockingQueue<Message> handlerQueue = postMap.get(path);

        if (null != handlerQueue)
        {
            HttpPostMessage postMessage = new HttpPostMessage(getQueue(), this, request, content, ctx);
            send(postMessage, handlerQueue);
        }
    }


    public void processPut(String url, HttpRequest request) {
    }

    public void processDelete(String url, HttpRequest request) {
    }
}
