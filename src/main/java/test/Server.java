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
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


/**
 * Created by Clark on 2/4/2017.
 */
public class Server {
    private Logger logger = Logger.getLogger(Server.class);

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
        private static Logger logger = Logger.getLogger(LocalChannelInitializer.class);

        private SslContext sslContext;

        public LocalChannelInitializer (SslContext sslContext) {
            this.sslContext = sslContext;
        }

        protected void initChannel(NioSocketChannel serverSocketChannel) throws Exception {
            logger.info("Got connection from " + serverSocketChannel.remoteAddress());

            SslHandler sslHandler = sslContext.newHandler(serverSocketChannel.alloc());
            serverSocketChannel.pipeline().addLast(sslHandler);

            EchoHandler echoHandler = new EchoHandler();
            serverSocketChannel.pipeline().addLast(echoHandler);

            // ChannelHandler channelHandler = new LocalChannelHandler ();
            // serverSocketChannel.pipeline().addLast(channelHandler);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.fatal("Caught exception, exiting", cause);
            ctx.close();

            System.exit(1);
        }
    }

    public static void main (String[] argv)
    {
        Server server = new Server();
        server.go();
    }


    public void go () {
        String dir = "C:\\Users\\Clark\\IdeaProjects\\miranda\\data\\";

        String trustStoreFilename = dir + "truststore";
        String trustStorePassword = "whatever";
        String trustStoreAlias = "ca";

        String log4jConfigurationFile = dir + "log4j.xml";

        DOMConfigurator.configure(log4jConfigurationFile);

        String keyStoreFilename = dir + "serverkeystore";
        String keyStorePassword = "whatever";
        String keyStoreAlias = "server";

        int port = 6789;

        SslContext sslContext = com.ltsllc.miranda.network.Utils.createServerSslContext(keyStoreFilename, keyStorePassword, keyStoreAlias, trustStoreFilename, trustStorePassword, trustStoreAlias);
        LocalChannelInitializer localChannelInitializer = new LocalChannelInitializer(sslContext);
        ServerBootstrap serverBootstrap = Util.createServerBootstrap(localChannelInitializer);

        System.out.println ("Listening on " + port);

        serverBootstrap.bind(port);
    }
}
