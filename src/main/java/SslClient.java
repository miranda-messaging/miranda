import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Clark on 1/30/2017.
 */
public class SslClient {
    private ChannelHandlerContext channelHandlerContext;


    public static class LocalInitializer extends ChannelInitializer<SocketChannel> {
        private SslContext sslContext;
        private String message;

        public LocalInitializer(SslContext sslContext, String message) {
            this.sslContext = sslContext;
            this.message = message;
        }

        public void initChannel(SocketChannel sc) {
            SslHandler sslHandler = sslContext.newHandler(sc.alloc());
            sc.pipeline().addLast(sslHandler);
            LocalHandler localHandler = new LocalHandler(message);
            sc.pipeline().addLast(localHandler);
        }
    }

    private static class LocalHandler extends ChannelInboundHandlerAdapter {
        private String message;

        public LocalHandler (String message) {
            this.message = message;
        }

        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] array = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, array);
            String s = new String(array);

            if (s.equals(message))
                System.out.println("It worked!");
            else
                System.out.println("Failure");

            System.exit(0);
        }
    }


    public static void main(String[] argv) {
        DOMConfigurator.configure("log4j.xml");
        SslClient sslClient = new SslClient();
        sslClient.doit();
    }

    public void doit() {
        try {
            Bootstrap bootstrap = new Bootstrap();

            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            bootstrap.group(eventLoopGroup);

            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            String message = "Hello world!";
            SslContext serverContext = Utils.createSslContext();

            LocalInitializer localHandler = new LocalInitializer(serverContext, message);
            bootstrap.handler(localHandler);

            String host = "localhost";
            int port = 6789;
            ByteBuf byteBuf = Unpooled.directBuffer(256);
            ByteBufUtil.writeUtf8(byteBuf, message);
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.channel().writeAndFlush(byteBuf);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
