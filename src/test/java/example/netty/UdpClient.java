package example.netty;

import java.net.InetSocketAddress;
import java.util.UUID;

import org.gora.server.common.NetworkUtils;
import org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket;
import org.gora.server.model.network.TestProtoBuf;
import org.gora.server.model.network.eServiceType;

import com.google.protobuf.ByteString;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class UdpClient {
    public void run(int port) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(final Channel ch) throws Exception {
                            ch.pipeline().addLast(new UdpClientHandler());
                        }
                    });

            // 서버에서 클라이언트에게 보내야할때 bind해놓음
            Channel server = bootstrap.bind(11112).sync().channel();

            NioEventLoopGroup eventLoopGroupConnetedChannel = new NioEventLoopGroup(3);
            Bootstrap bootstrapConnetedChannel = new Bootstrap();
            bootstrapConnetedChannel.group(eventLoopGroupConnetedChannel)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        public void initChannel(final Channel ch) throws Exception {
                            ch.pipeline().addLast(new UdpClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrapConnetedChannel.connect("127.0.0.1", port).sync();
            Channel clientToServerChanel = channelFuture.channel();

            StringBuilder tempMsg = new StringBuilder(UUID.randomUUID().toString());
            for (int i = 0; i < 30; i++) {
                UUID.randomUUID();
                tempMsg.append(UUID.randomUUID().toString());
            }

            for (int i = 0; i < 1; i++) {
                // 데이터 준비
                TestProtoBuf.Test test = TestProtoBuf.Test.newBuilder()
                        .setMsg(ByteString.copyFrom(tempMsg.toString().getBytes())).build();
                byte[] testBytes = test.toByteArray();

                // 패킷 분할생성
                NetworkPacket packet = NetworkUtils.getPacket(testBytes,
                        eServiceType.test);
                if (packet == null) {
                    System.out.println("에러발생");
                    return;
                }

                // 전송
                byte[] buffer = packet.toByteArray();
                if (buffer.length != NetworkUtils.TOTAL_MAX_SIZE) {
                    System.out.println("사이즈가 잘못됨");
                    return;
                }
                ByteBuf bytebuf = Unpooled.wrappedBuffer(buffer);
                clientToServerChanel.writeAndFlush(new DatagramPacket(bytebuf,
                        new InetSocketAddress("localhost", port))).addListeners(future -> {
                            if (!future.isSuccess()) {
                                future.cause().printStackTrace();
                                System.out.println("실패함");
                            }
                        });

            }
            Thread.sleep(30000);
            clientToServerChanel.closeFuture();
        } finally

        {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new UdpClient().run(11111);
    }
}
