package test;

import com.ltsllc.miranda.network.Utils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Created by Clark on 2/4/2017.
 */
public class Client {
    private static Logger logger = Logger.getLogger(Client.class);

    public static class LocalChannelHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] buffer = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, buffer);
            String s = new String(buffer);
            logger.info ("got " + s);

            // String message = "hi there";
            // byteBuf = Unpooled.directBuffer(256);
            // ByteBufUtil.writeUtf8(byteBuf, message);
            // ctx.writeAndFlush(byteBuf);
            // System.exit(0);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            logger.info ("channelInactive, exiting");
            ctx.close();
            System.exit(0);
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.fatal ("Caught exception, exiting", cause);

            ctx.close();
            System.exit(1);
        }
    }

    public static class LocalChannelFutureListener implements ChannelFutureListener {
        private SslContext sslContext;

        public LocalChannelFutureListener(SslContext sslContext) {
            this.sslContext = sslContext;
        }

        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            if (!channelFuture.isSuccess()) {
                channelFuture.cause().printStackTrace();
                System.exit(1);
            }

            SslHandler sslHandler = sslContext.newHandler(channelFuture.channel().alloc());
            channelFuture.channel().pipeline().addLast(sslHandler);

            LocalChannelHandler localChannelHandler = new LocalChannelHandler();
            channelFuture.channel().pipeline().addLast(localChannelHandler);

            String message = "Hello world!";
            ByteBuf byteBuf = Unpooled.directBuffer(256);
            ByteBufUtil.writeUtf8(byteBuf, message);

            logger.info ("Sending " + message);

            channelFuture.channel().writeAndFlush(byteBuf);
        }
    }

    public static void main(String[] argv) {
        Client client = new Client();
        client.go();
    }


    public void go () {
        String dir = "C:\\Users\\Clark\\IdeaProjects\\miranda\\data\\";

        String log4jConfigurationFile = dir + "log4j.xml";
        String trustStoreFilename = dir + "truststore";
        String trustStorePassword = "whatever";
        String trustStoreAlias = "ca";

        DOMConfigurator.configure(log4jConfigurationFile);

        LocalChannelHandler localChannelHandler = new LocalChannelHandler();

        SslContext sslContext = Utils.createClientSslContext(trustStoreFilename, trustStorePassword, trustStoreAlias);
        Bootstrap bootstrap = Util.createClientBootstrap(localChannelHandler);
        LocalChannelFutureListener localChannelFutureListener = new LocalChannelFutureListener(sslContext);

        String host = "localhost";
        int port = 6789;
        logger.info ("connecting to " + host + ":" + port);
        ChannelFuture channelFuture = bootstrap.connect (host, port);
        channelFuture.addListener(localChannelFutureListener);

        // sleep(5000);

        // ByteBuf byteBuf = Unpooled.directBuffer(256);
        // ByteBufUtil.writeUtf8(byteBuf, "whatever");
        // channelFuture.channel().writeAndFlush(byteBuf);
    }

    private void sleep (long period) {

        synchronized (Thread.currentThread()) {
            try {
                Thread.currentThread().wait(period);
            } catch (InterruptedException e) {
            }
        }
    }
}
