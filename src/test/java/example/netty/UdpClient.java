package example.netty;

import java.net.InetSocketAddress;

import org.gora.server.model.CommonData;
import org.gora.server.model.eServiceRouteType;

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

public class UdpClient {

    public static void main(String[] args) throws Exception {
        new UdpClient().run(11111);
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

            // 서버에서 클라이언트에게 보내야할때 bind해놓음 
            Channel ch = b.bind(11112).sync().channel();
            

            for (int i = 0; i < 5; i++) {
            CommonData commonData = new CommonData(i+": send to udp server", eServiceRouteType.player_coordinate);

            ObjectMapper objectMapper = new ObjectMapper();
            byte[] sendBytes = objectMapper.writeValueAsBytes(commonData);
            ch.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(sendBytes),
                    new InetSocketAddress("localhost", port))).sync();
            }
            
            ch.closeFuture().await(1000 * 20);
        } finally {
            group.shutdownGracefully();
        }
    }
}
