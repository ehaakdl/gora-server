package example.netty;

import org.gora.server.model.CommonData;
import org.gora.server.model.eCodeType;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public final class TcpClient {
    static final String HOST = "127.0.0.1";
    static final int PORT = 11200;

    public static void main(String[] args) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TcpClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();


            // Write a message
            for (int i = 0; i < 5; i++) {
            
            CommonData commonData = new CommonData(i+": sned to tcp server", eCodeType.tcp, null);
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] message = objectMapper.writeValueAsString(commonData).getBytes();
            ByteBuf buffer = Unpooled.wrappedBuffer(message);
            future.channel().writeAndFlush(buffer).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
            
            future.channel().closeFuture().await(1000*20);
        } finally {
            group.shutdownGracefully();
        }
    }

}