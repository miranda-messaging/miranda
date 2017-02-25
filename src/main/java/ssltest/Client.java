package ssltest;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import javax.net.ssl.TrustManagerFactory;
import java.security.cert.X509Certificate;

/**
 * Created by Clark on 2/4/2017.
 */
public class Client {
    private static Logger logger = Logger.getLogger(Client.class);

    public static class LocalChannelHandler extends ChannelInboundHandlerAdapter {
        private SslContext sslContext;

        public LocalChannelHandler (SslContext sslContext) {
            this.sslContext = sslContext;
        }

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

            channelFuture.channel().pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));

            SslHandler sslHandler = sslContext.newHandler(channelFuture.channel().alloc());
            channelFuture.channel().pipeline().addLast(sslHandler);

            // LocalChannelHandler localChannelHandler = new LocalChannelHandler();
            // channelFuture.channel().pipeline().addLast(localChannelHandler);

            String message = "Hello world!";
            ByteBuf byteBuf = Unpooled.directBuffer(256);
            ByteBufUtil.writeUtf8(byteBuf, message);

            logger.info ("Sending " + message);

            channelFuture.channel().writeAndFlush(byteBuf);
        }
    }

    public static class LocalChannelIniatizer extends ChannelInitializer<SocketChannel> {
        private SslContext sslContext;

        public LocalChannelIniatizer (SslContext sslContext) {
            this.sslContext = sslContext;
        }

        public void initChannel (SocketChannel socketChannel) throws Exception{
            SslHandler sslHandler = sslContext.newHandler(socketChannel.alloc());
            socketChannel.pipeline().addLast(sslHandler);
        }
    }




    public static void main(String[] argv) {
        Client client = new Client();
        try {
            client.go();
        } catch (Exception e) {
            logger.fatal("Exception", e);
        }
    }


    public void go () throws Exception {
        String dir = "C:\\Users\\Clark\\IdeaProjects\\miranda\\";

        String log4jConfigurationFile = dir + "log4j.xml";

        DOMConfigurator.configure(log4jConfigurationFile);

        String trustStoreFilename = dir + "truststore";
        String trustStorePassword = "whatever";
        String trustStoreAlias = "ca";

        String serverCertificateFilename = dir + "serverkeystore";
        String serverCertificatePassword = "whatever";
        String serverCertificateAlias = "ca";

        X509Certificate certificate = Util.getCertificate(serverCertificateFilename, trustStorePassword, trustStoreAlias);


        // SslContext sslContext = Util.createClientSslContext(certificate);
        SslContext sslContext = Util.createClientSslContext(trustStoreFilename, trustStorePassword, trustStoreAlias);

        LocalChannelIniatizer localChannelIniatizer = new LocalChannelIniatizer(sslContext);
        Bootstrap bootstrap = Util.createClientBootstrap(localChannelIniatizer);

        String host = "localhost";
        int port = 6789;
        logger.info ("connecting to " + host + ":" + port);
        Channel channel = bootstrap.connect(host, port).sync().channel();

        String message = "Hello world!";
        ByteBuf byteBuf = Unpooled.directBuffer(256);
        ByteBufUtil.writeUtf8(byteBuf, message);
        channel.writeAndFlush(byteBuf).sync();
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
