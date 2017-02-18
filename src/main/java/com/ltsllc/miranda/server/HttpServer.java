package com.ltsllc.miranda.server;

/**
 * Created by Clark on 2/10/2017.
 */

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Utils;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.user.HttpActionHandler;
import com.ltsllc.miranda.user.PostHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * The HTTP server for the system.
 */
public class HttpServer extends Consumer {
    private class LocalChannelInitializer extends ChannelInitializer<SocketChannel> {
        private SslContext sslContext;
        private HttpServer server;

        public LocalChannelInitializer(SslContext sslContext, HttpServer server) {
            this.sslContext = sslContext;
            this.server = server;
        }

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            SslHandler sslHandler = sslContext.newHandler(socketChannel.alloc());
            // socketChannel.pipeline().addLast(sslHandler);

            socketChannel.pipeline().addLast(new HttpServerCodec());

            HttpServerHandler httpServerHandler = new HttpServerHandler(server);
            socketChannel.pipeline().addLast(httpServerHandler);
        }
    }

    private Logger logger = Logger.getLogger(HttpServer.class);

    private Map<String, BlockingQueue<Message>> postMap = new HashMap<String, BlockingQueue<Message>>();
    private int port;

    public HttpServer (int port) {
        super("httpServer");

        this.port = port;
    }

    public void startup() {

        MirandaProperties properties = MirandaProperties.getInstance();

        SslContext sslContext = Utils.createServerContext(
                properties.getProperty(MirandaProperties.PROPERTY_KEY_STORE),
                properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD),
                properties.getProperty(MirandaProperties.PROPERTY_KEY_STORE_ALIAS),
                properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE),
                properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD),
                properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_ALIAS)
        );

        LocalChannelInitializer localChannelInitializer = new LocalChannelInitializer(sslContext, this);
        ServerBootstrap serverBootstrap = Utils.createServerBootstrap(localChannelInitializer);

        serverBootstrap.bind(port);
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
