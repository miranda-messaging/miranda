package com.ltsllc.miranda.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;

/**
 * Created by Clark on 2/10/2017.
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = Logger.getLogger(HttpServerHandler.class);

    private HttpServer server;

    public HttpServerHandler (HttpServer server) {
        this.server = server;
    }

    private ByteArrayOutputStream byteArrayOutputStream = null;
    private HttpMethod lastMethod = null;
    private HttpRequest lastRequest = null;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof LastHttpContent)
        {
            LastHttpContent lastHttpContent = (LastHttpContent) msg;
            byte[] buffer = new byte[lastHttpContent.content().readableBytes()];
            lastHttpContent.content().getBytes(0, buffer);
            byteArrayOutputStream.write(buffer);
            String s = new String(byteArrayOutputStream.toByteArray());
            server.processPost(lastRequest.uri(), lastRequest, s, ctx);
        }
        else if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            HttpMethod method = request.method();

            if (method == HttpMethod.GET)
                server.processGet(request.uri(), request);
            else if (method == HttpMethod.PUT)
                server.processPut(request.uri(), request);
            else if (method == HttpMethod.DELETE)
                server.processDelete(request.uri(), request);
            else if (method == HttpMethod.POST) {
                lastRequest = request;
                lastMethod = method;
                byteArrayOutputStream = new ByteArrayOutputStream();
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        ctx.writeAndFlush(response);
        ctx.close();
        logger.info("read complete, closing channel");
    }
}
