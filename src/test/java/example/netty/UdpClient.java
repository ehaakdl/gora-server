package example.netty;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.gora.server.model.network.NetworkPacketProtoBuf.NetworkPacket;
import org.gora.server.common.utils.AesUtils;
import org.gora.server.common.utils.NetworkUtils;
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
    public static String channelId = NetworkUtils.UDP_EMPTY_CHANNEL_ID;

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

            // udp 연결 초기화(식별 키 받는 패킷 전송)
            NetworkPacket packet = NetworkUtils.getEmptyData(
                    eServiceType.udp_initial);
            if (packet == null) {
                System.out.println("에러발생");
                return;
            }
            send(packet, clientToServerChanel, port);

            boolean isUdpInitial = false;
            while (!isUdpInitial) {
                if (!channelId.equals(NetworkUtils.UDP_EMPTY_CHANNEL_ID)) {
                    System.out.println("udp init: " + AesUtils.decrypt(channelId));
                    isUdpInitial = true;
                    break;
                }

                Thread.sleep(1000);
            }

            List<NetworkPacket> packets;
            for (int i = 0; i < 10; i++) {

                // 데이터 준비
                TestProtoBuf.Test test = TestProtoBuf.Test.newBuilder()
                        .setMsg(ByteString.copyFrom(tempMsg.toString().getBytes())).build();
                byte[] testBytes = test.toByteArray();

                // 패킷 분할생성
                packets = NetworkUtils.generateSegmentPacket(testBytes,
                        eServiceType.test, NetworkUtils.generateIdentify(), testBytes.length, channelId);

                // 전송
                for (int j = 0; j < packets.size(); j++) {
                    packet = packets.get(j);
                    send(packet, clientToServerChanel, port);
                }

            }

            Scanner scanner = new Scanner(System.in);
            System.out.println("종료하고 싶다면 엔터를");
            scanner.nextLine();
            clientToServerChanel.closeFuture();
        } finally

        {
            group.shutdownGracefully();
        }
    }

    private void send(NetworkPacket packet, Channel channel, int port) {
        byte[] buffer = packet.toByteArray();
        if (buffer.length != NetworkUtils.TOTAL_MAX_SIZE) {
            System.out.println("사이즈가 잘못됨");
            return;
        }
        ByteBuf bytebuf = Unpooled.wrappedBuffer(buffer);
        channel.writeAndFlush(new DatagramPacket(bytebuf,
                new InetSocketAddress("localhost", port))).addListeners(future -> {
                    if (!future.isSuccess()) {
                        future.cause().printStackTrace();
                        System.out.println("실패함");
                    }
                });
    }

    public static void main(String[] args) throws Exception {
        new UdpClient().run(11111);
    }
}
