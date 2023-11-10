package example.netty;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;

import org.gora.server.common.CommonUtils;
import org.gora.server.common.NetworkUtils;
import org.gora.server.model.network.NetworkPakcetProtoBuf;
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
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

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
                            ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
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
                            ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast(new UdpClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrapConnetedChannel.connect("127.0.0.1", port).sync();
            Channel clientToServerChanel = channelFuture.channel();

            StringBuilder tempMsg = new StringBuilder(UUID.randomUUID().randomUUID().toString());
            for (int i = 0; i < 30; i++) {
                tempMsg.append(UUID.randomUUID().randomUUID().toString());
            }

            for (int i = 0; i < 3000000; i++) {
                // 데이터 준비
                TestProtoBuf.Test test = TestProtoBuf.Test.newBuilder()
                        .setMsg(ByteString.copyFrom(tempMsg.toString().getBytes())).build();
                byte[] testBytes = CommonUtils.objectToBytes(test);

                // 패킷 분할생성
                List<NetworkPakcetProtoBuf.NetworkPacket> packets = NetworkUtils.getSegment(testBytes,
                        eServiceType.test, NetworkUtils.getIdentify());
                if (packets == null) {
                    System.out.println("에러발생");
                    return;
                }

                // 전송
                for (int index = 0; index < packets.size(); index++) {
                    byte[] buffer = CommonUtils.objectToBytes(packets.get(index));
                    if (buffer.length != NetworkUtils.TOTAL_MAX_SIZE) {
                        System.out.println("사이즈가 잘못됨");
                        return;
                    }
                    ByteBuf bytebuf = Unpooled.wrappedBuffer(buffer);
                    clientToServerChanel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bytebuf),
                            new InetSocketAddress("localhost", port))).addListeners(future -> {
                                if (!future.isSuccess()) {
                                    future.cause().printStackTrace();
                                    System.out.println("실패함");
                                }
                            });
                }
            }

            clientToServerChanel.closeFuture();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new UdpClient().run(11111);
    }
}
