package org.gora.server.component.network;


import java.net.InetSocketAddress;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UdpServer {
    private EventLoopGroup bossLoopGroup;
    private ChannelGroup channelGroup;
    private final UdpInboundHandler udpServerInboundHandler;
    
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

    public boolean send(String ip, int port, byte[] data){
        channelGroup.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(data),
                    new InetSocketAddress(ip, port)));
        
                    return true;
    }

    public void shutdown() {
        channelGroup.close();
        bossLoopGroup.shutdownGracefully();
    }

}
