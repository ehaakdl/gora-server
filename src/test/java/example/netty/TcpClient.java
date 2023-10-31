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

import io.jsonwebtoken.io.IOException;
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
    private void start() throws InterruptedException, IOException, java.io.IOException {
        String uuid = UUID.randomUUID().randomUUID().toString();
        StringBuilder tempMsg = new StringBuilder(uuid);
        for (int i = 0; i < 30; i++) {
            tempMsg.append(uuid);
        }
        
        int MAX_SEND_PACKET_COUNT = 800;
        for (int i = 0; i < MAX_SEND_PACKET_COUNT; i++) {
            // 데이터 분할 생성
            TestProtoBuf.Test test = TestProtoBuf.Test.newBuilder()
                    .setMsg(ByteString.copyFrom(tempMsg.toString().getBytes())).build();
            byte[] testBytes = CommonUtils.objectToBytes(test);
            List<NetworkPakcetProtoBuf.NetworkPacket> packets = NetworkUtils.getSegment(testBytes,
                    eServiceType.test, NetworkUtils.getIdentify(), 0, null);
            if (packets == null) {
                System.out.println("에러발생");
                return;
            }

            // 전송
            for (int index = 0; index < packets.size(); index++) {
                ByteBuf buffer = Unpooled.wrappedBuffer(CommonUtils.objectToBytes(packets.get(index)));
                serverChannel.writeAndFlush(buffer).sync();
            }
            sendPacketLog = sendPacketLog + packets.size();
            System.out.println(sendPacketLog); 
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
