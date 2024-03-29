package example.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.gora.server.common.utils.NetworkUtils;
import com.gora.server.model.network.eServiceType;
import com.gora.server.model.network.protobuf.NetworkPacketProtoBuf.NetworkPacket;
import com.gora.server.model.network.protobuf.TestProtoBuf;

import com.google.protobuf.ByteString;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
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

    static int sendPacketLog = 0;
    static int size;

    private void start() throws InterruptedException, IOException {
        UUID.randomUUID();
        String uuid = UUID.randomUUID().toString();
        StringBuilder tempMsg = new StringBuilder(uuid);
        for (int i = 0; i < 100000; i++) {
            tempMsg.append(uuid);
        }

        int MAX_SEND_PACKET_COUNT = 10;
        List<NetworkPacket> packets;
        String identify = NetworkUtils.generateIdentify();
        // 데이터 준비
        TestProtoBuf.Test test = TestProtoBuf.Test.newBuilder()
                .setMsg(ByteString.copyFrom(tempMsg.toString().getBytes())).build();
        byte[] testBytes = test.toByteArray();
        // for (int i = 0; i < MAX_SEND_PACKET_COUNT; i++) {

        // 패킷 분할생성
        packets = NetworkUtils.generateSegmentPacket(testBytes,
                eServiceType.test, identify, testBytes.length, NetworkUtils.UDP_EMPTY_CHANNEL_ID);
        byte[] data;
        for (int i = 0; i < packets.size(); i++) {
            data = NetworkUtils.removePadding(packets.get(i).getData().toByteArray(),
                    NetworkUtils.DATA_MAX_SIZE - packets.get(i).getDataSize());
            size += data.length;
        }

        // 전송
        for (int cout = 0; cout < packets.size(); cout++) {
            byte[] buffer = packets.get(cout).toByteArray();
            if (buffer.length != NetworkUtils.TOTAL_MAX_SIZE) {
                System.out.println("사이즈가 잘못됨");
                return;
            }
            ByteBuf bytebuf = Unpooled.wrappedBuffer(buffer);
            serverChannel.writeAndFlush(bytebuf).sync();
        }
        // }

    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

    public static class ClientThread extends Thread {
        @Override
        public void run() {
            TcpClient client = new TcpClient("127.0.0.1", SERVER_PORT);
            try {
                client.connect();
                client.start();
                Thread.sleep(200000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client.close();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int maxClientCount = 1;
        List<ClientThread> clientThreadList = new ArrayList<>();
        for (int count = 0; count < maxClientCount; count++) {
            clientThreadList.add(new ClientThread());
            clientThreadList.get(count).start();
            Thread.sleep(2000);
        }

        for (int count = 0; count < maxClientCount; count++) {
            // 공유자원 때문에 예외발생
            synchronized (clientThreadList.get(count)) {
                clientThreadList.get(count).wait();
            }
        }
    }
}
