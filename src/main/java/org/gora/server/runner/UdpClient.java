package org.gora.server.runner;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.Getter;
import org.gora.server.service.UdpServerHandler;


@Getter
public class UdpClient {
    private final EventLoopGroup eventLoopGroup;
    private final boolean isConnected;
    private final ChannelFuture channelFuture;

    public UdpClient(String clientIp, int port) throws InterruptedException {
        this.eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.handler(new UdpServerHandler());
        bootstrap.channel(NioDatagramChannel.class);
        this.channelFuture =  bootstrap.connect(clientIp, port).sync();
        Thread.sleep(10);
        this.isConnected = channelFuture.isSuccess();
    }

    public void shutdown(){
        eventLoopGroup.shutdownGracefully();
    }
}
