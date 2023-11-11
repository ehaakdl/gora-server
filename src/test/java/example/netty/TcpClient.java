package example.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
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

    private void start() throws InterruptedException, IOException {
        String uuid = UUID.randomUUID().randomUUID().toString();
        StringBuilder tempMsg = new StringBuilder(uuid);
        for (int i = 0; i < 30; i++) {
            tempMsg.append(uuid);
        }

        int MAX_SEND_PACKET_COUNT = 100000000;
        for (int i = 0; i < MAX_SEND_PACKET_COUNT; i++) {
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
                serverChannel.writeAndFlush(bytebuf).sync();
            }
            sendPacketLog = sendPacketLog + packets.size();
        }
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
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client.close();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int maxClientCount = 100;
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
