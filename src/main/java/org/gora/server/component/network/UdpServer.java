package org.gora.server.component.network;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UdpServer {
    private EventLoopGroup bossLoopGroup;
    private ChannelGroup channelGroup;
    private final UdpServerInboundHandler udpServerInboundHandler;

    @PostConstruct
    public void init(){
        this.bossLoopGroup = new NioEventLoopGroup();
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Async
    public void startup(int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.AUTO_CLOSE, true)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    public void initChannel(final Channel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(udpServerInboundHandler);
                    }
                });
        ;
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        channelGroup.add(channelFuture.channel());
    }

    public void shutdown() {
        channelGroup.close();
        bossLoopGroup.shutdownGracefully();
    }

}
