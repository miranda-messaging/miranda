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

/**
 * Created by Clark on 2/4/2017.
 */
public class Client {

    public static class LocalChannelHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] buffer = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, buffer);
            String s = new String(buffer);
            System.out.println ("got " + s);

            String message = "hi there";
            byteBuf = Unpooled.directBuffer(256);
            ByteBufUtil.writeUtf8(byteBuf, message);
            ctx.writeAndFlush(byteBuf);
            // System.exit(0);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println ("conection closed");
            ctx.close();
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
        }
    }

    public static void main(String[] argv) {
        Client client = new Client();
        client.go();
    }


    public void go () {
        String trustStoreFilename = "c:\\users\\clark\\ideaprojects\\miranda\\data\\truststore";
        String trustStorePassword = "whatever";
        String trustStoreAlias = "ca";

        LocalChannelHandler localChannelHandler = new LocalChannelHandler();

        SslContext sslContext = Utils.createClientSslContext(trustStoreFilename, trustStorePassword, trustStoreAlias);
        Bootstrap bootstrap = Util.createClientBootstrap(localChannelHandler);
        LocalChannelFutureListener localChannelFutureListener = new LocalChannelFutureListener(sslContext);

        String host = "192.168.1.100";
        int port = 6789;
        System.out.println ("connecting to " + host + ":" + port);
        ChannelFuture channelFuture = bootstrap.connect ("192.168.1.100", 6789);
        channelFuture.addListener(localChannelFutureListener);

        sleep(5000);

        ByteBuf byteBuf = Unpooled.directBuffer(256);
        ByteBufUtil.writeUtf8(byteBuf, "whatever");
        channelFuture.channel().writeAndFlush(byteBuf);
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
