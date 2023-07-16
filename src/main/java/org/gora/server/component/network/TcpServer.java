package org.gora.server.component.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

//todo 클라이언트 세션을 중복 보관하지 않는 방법 생각해보기
@Component
@RequiredArgsConstructor
@Slf4j
public class TcpServer {
    private EventLoopGroup bossLoopGroup;
    private EventLoopGroup workerGroup;
    private final TcpServerHandler tcpServerHandler;
    @Value("${app.max_client}")
    private int maxClient;

    @PostConstruct
    public void init() {
        this.bossLoopGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
    }

    @Async
    public void startup(int port) throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossLoopGroup, workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.localAddress(new InetSocketAddress("127.0.0.1", port));
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(tcpServerHandler);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, maxClient);  //동시 접속 수
        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        channelFuture.channel().closeFuture().sync();
    }

    public void shutdown() {
        this.bossLoopGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }
}
