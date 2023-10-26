package example.netty;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.UUID;

import org.gora.server.model.network.NetworkPacketProtoBuf;
import org.gora.server.model.network.NetworkTestProtoBuf;
import org.gora.server.model.network.eServiceRouteTypeProtoBuf;

import io.jsonwebtoken.io.IOException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

public class TcpClient {
    private static final int SERVER_PORT = 11200;
    private final String host;
    private final int port;

    private Channel serverChannel;
    private EventLoopGroup eventLoopGroup;

    public TcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws InterruptedException {
        eventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("client"));

        Bootstrap bootstrap = new Bootstrap().group(eventLoopGroup);

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(new InetSocketAddress(host, port));
        bootstrap.handler(new TcpClientInitializer());

        serverChannel = bootstrap.connect().sync().channel();
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

    private void start() throws InterruptedException, IOException, java.io.IOException {
        try (Scanner scanner = new Scanner(System.in)) {
            String message;
            ChannelFuture future;

            // while(true) {
                // 사용자 입력
                // message = scanner.nextLine();

                // Server로 전송
                StringBuilder test = new StringBuilder(UUID.randomUUID().randomUUID().toString());
                for (int i = 0; i < 30; i++) {
                    test.append(UUID.randomUUID().randomUUID().toString());
                }
                for (int i = 0; i < 2; i++) {
                    NetworkTestProtoBuf.NetworkTest playerCoordinateProtoBuf = NetworkTestProtoBuf.NetworkTest.newBuilder().setA(test.toString()).setB(test.toString()).build();
                    byte[] playerCoordinateBytes= objectToBytes(playerCoordinateProtoBuf);

                    eServiceRouteTypeProtoBuf.eServiceRouteType routeType = eServiceRouteTypeProtoBuf.eServiceRouteType.health_check;
                    NetworkPacketProtoBuf.NetworkPacket networkPacket = NetworkPacketProtoBuf.NetworkPacket.newBuilder().setTotalSize(1234).setType(routeType).build();
                
                    byte[] networkPacketBytes = objectToBytes(networkPacket);
                    System.out.println(networkPacketBytes.length);
                
                    ByteBuf buffer = Unpooled.wrappedBuffer(networkPacketBytes);
                    future = serverChannel.writeAndFlush(buffer);

                    // if("quit".equals(message)){
                    //     serverChannel.closeFuture().sync();
                    //     break;
                    // }
                }
            // }
        }
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        TcpClient client = new TcpClient("127.0.0.1", SERVER_PORT);

        try {
            client.connect();
            client.start();
        } finally {
            client.close();
        }
    }

}
