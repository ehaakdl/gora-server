package example.netty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;

import org.gora.server.model.network.NetworkPacketProtoBuf;
import org.gora.server.model.network.PlayerCoordinateProtoBuf;
import org.gora.server.model.network.eServiceRouteTypeProtoBuf;

import com.google.protobuf.ByteString;

import io.jsonwebtoken.io.IOException;
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
            

            for (int i = 0; i < 5; i++) {
                PlayerCoordinateProtoBuf.PlayerCoordinate playerCoordinateProtoBuf = PlayerCoordinateProtoBuf.PlayerCoordinate.newBuilder().setX(1).setY(2).build();
                byte[] playerCoordinateBytes= objectToBytes(playerCoordinateProtoBuf);

                eServiceRouteTypeProtoBuf.eServiceRouteType routeType = eServiceRouteTypeProtoBuf.eServiceRouteType.player_coordinate;
                NetworkPacketProtoBuf.NetworkPacket networkPacket = NetworkPacketProtoBuf.NetworkPacket.newBuilder().setData(ByteString.copyFrom(playerCoordinateBytes)).setTotalSize(11111).setType(routeType).build();
            
            ch.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(objectToBytes(networkPacket)),
                    new InetSocketAddress("localhost", port))).sync();
            }
            
            ch.closeFuture().await(1000 * 20);
        } finally {
            group.shutdownGracefully();
        }
    }
}
