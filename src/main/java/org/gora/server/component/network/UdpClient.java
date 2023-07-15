package org.gora.server.component.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;


@Getter
public class UdpClient {
    private final EventLoopGroup eventLoopGroup;
    private final ChannelFuture channelFuture;

    public UdpClient(String clientIp, int port, @Autowired UdpInboundHandler udpInboundHandler) throws InterruptedException {
        this.eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.handler(new ChannelInitializer<>() {
            @Override
            public void initChannel(final Channel ch) {
                ChannelPipeline p = ch.pipeline();
                p.addLast(udpInboundHandler);
            }
        });
        bootstrap.channel(NioDatagramChannel.class);
        this.channelFuture =  bootstrap.connect(clientIp, port).sync();
        Thread.sleep(10);
    }

    public void shutdown(){
        eventLoopGroup.shutdownGracefully();
    }
}
