package test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;


/**
 * Created by Clark on 2/4/2017.
 */
public class Server {


    public static class LocalChannelHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] buffer = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, buffer);
            String s = new String(buffer);
            System.out.println ("got " + s);
        }
    }

    public static class LocalChannelInitializer extends ChannelInitializer<NioSocketChannel> {
        private SslContext sslContext;

        public LocalChannelInitializer (SslContext sslContext) {
            this.sslContext = sslContext;
        }

        protected void initChannel(NioSocketChannel serverSocketChannel) throws Exception {
            SslHandler sslHandler = sslContext.newHandler(serverSocketChannel.alloc());
            serverSocketChannel.pipeline().addLast(sslHandler);

            ChannelHandler channelHandler = new LocalChannelHandler ();
            serverSocketChannel.pipeline().addLast(channelHandler);
        }
    }
    public static void main (String[] argv)
    {
        Server server = new Server();
        server.go();
    }


    public void go () {
        String trustStoreFilename = "c:\\users\\clark\\ideaprojects\\miranda\\data\\truststore";
        String trustStorePassword = "whatever";
        String trustStoreAlias = "ca";

        String keyStoreFilename = "c:\\users\\clark\\ideaprojects\\mirana\\data\\serverkeystore";
        String keyStorePassword = "whatever";
        String keyStoreAlias = "server";

        SslContext sslContext = com.ltsllc.miranda.network.Utils.createServerSslContext(keyStoreFilename, keyStorePassword, keyStoreAlias, trustStoreFilename, trustStorePassword, trustStoreAlias);
        LocalChannelInitializer localChannelInitializer = new LocalChannelInitializer(sslContext);
        ServerBootstrap serverBootstrap = Util.createServerBootstrap(localChannelInitializer);

        serverBootstrap.bind(6789);
    }
}
