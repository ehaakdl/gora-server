package example.netty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import io.jsonwebtoken.io.IOException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class UdpClient {

    public static void main(String[] args) throws Exception {
        new UdpClient().run(11111);
    }
    public static Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException, java.io.IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }
    public static byte[] objectToBytes(Object obj) throws IOException, java.io.IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        }
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
            
            
            StringBuilder test = new StringBuilder(UUID.randomUUID().randomUUID().toString());
            for (int i = 0; i < 30; i++) {
                test.append(UUID.randomUUID().randomUUID().toString());
            }
            for (int i = 0; i < 2; i++) {
                // NetworkTestProtoBuf.NetworkTest playerCoordinateProtoBuf = NetworkTestProtoBuf.NetworkTest.newBuilder().setA(test.toString()).setB(test.toString()).build();
                // byte[] playerCoordinateBytes= objectToBytes(playerCoordinateProtoBuf);

                // eServiceRouteTypeProtoBuf.eServiceRouteType routeType = eServiceRouteTypeProtoBuf.eServiceRouteType.player_coordinate;
                // NetworkPacketProtoBuf.NetworkPacket networkPacket = NetworkPacketProtoBuf.NetworkPacket.newBuilder().setData(ByteString.copyFrom(playerCoordinateBytes)).setTotalSize(playerCoordinateBytes.length).setType(routeType).build();
                
                // byte[] networkPacketBytes = objectToBytes(networkPacket);
                // System.out.println(networkPacketBytes.length);
                // ch.writeAndFlush(new DatagramPacket(
                //     Unpooled.copiedBuffer(networkPacketBytes),
                //     new InetSocketAddress("localhost", port))).sync();

                // Thread.sleep(5);
            }
            
            ch.closeFuture().await(1000 * 20);
        } finally {
            group.shutdownGracefully();
        }
    }
}
