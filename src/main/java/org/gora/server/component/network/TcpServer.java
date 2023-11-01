package org.gora.server.component.network;

import java.net.InetSocketAddress;

import org.gora.server.component.network.pipline.TcpPiplineInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

//todo 클라이언트 세션을 중복 보관하지 않는 방법 생각해보기
@Component
@RequiredArgsConstructor
public class TcpServer {
    // 클라이언트 연결을 담당하는 스레드 그룹
    private EventLoopGroup bossLoopGroup;
    // 클라이언트 패킷 수신을 담당하는 스레드 그룹
    private EventLoopGroup workerGroup;
    private final TcpPiplineInitializer piplineInitializer;
    @Value("${app.max_client}")
    private int maxClient;
    @Value("${app.tcp_accept_thread_count}")
    private int acceptThreadCount;
    @Value("${app.tcp_event_thread_count}")
    private int eventThreadCount;

    @PostConstruct
    public void init() {
        this.bossLoopGroup = new NioEventLoopGroup(acceptThreadCount);
        this.workerGroup = new NioEventLoopGroup(eventThreadCount);
    }

    @Async
    public void startup(int port) throws InterruptedException {        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossLoopGroup, workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.localAddress(new InetSocketAddress("127.0.0.1", port));
        serverBootstrap.childHandler(piplineInitializer)
                .option(ChannelOption.SO_BACKLOG, maxClient);  //동시 접속 수
        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        channelFuture.channel().closeFuture().sync();
    }

    public void shutdown() {
        this.bossLoopGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }
}
