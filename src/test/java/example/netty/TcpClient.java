package example.netty;



import java.net.InetSocketAddress;
import java.util.Scanner;

import org.gora.server.model.CommonData;
import org.gora.server.model.eServiceRouteType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private void start() throws InterruptedException, JsonProcessingException {
        try (Scanner scanner = new Scanner(System.in)) {
            String message;
            ChannelFuture future;

            while(true) {
                // 사용자 입력
                message = scanner.nextLine();

                // Server로 전송
                CommonData commonData = new CommonData(message, eServiceRouteType.player_coordinate, null);
                ObjectMapper objectMapper = new ObjectMapper();
                byte[] messageByte = objectMapper.writeValueAsString(commonData).getBytes();
                ByteBuf buffer = Unpooled.wrappedBuffer(messageByte);
                future = serverChannel.writeAndFlush(buffer);

                if("quit".equals(message)){
                    serverChannel.closeFuture().sync();
                    break;
                }
            }

            // 종료되기 전 모든 메시지가 flush 될때까지 기다림
            if(future != null){
                future.sync();
            }
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
