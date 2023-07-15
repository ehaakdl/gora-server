package example.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.gora.server.common.eEnv;
import org.gora.server.model.CommonData;
import org.gora.server.model.eCodeType;

import java.net.InetSocketAddress;
import java.util.Objects;

public class UdpClient {
    private ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        new UdpClient().run(eEnv.getDefaultIntTypeValue(eEnv.SERVER_PORT));
    }

    public void run(int port) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(final Channel ch) throws Exception {
                            ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast(new UdpClientHandler());
                        }
                    });

            Channel ch = b.bind(0).sync().channel();
            CommonData commonData = new CommonData(null, eCodeType.test, null, null);
            ch.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(Objects.requireNonNull(CommonData.serialization(commonData))),
                    new InetSocketAddress("localhost", port))).sync();

            if (!ch.closeFuture().await(15000)) {
                System.err.println("Request timed out.");
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
