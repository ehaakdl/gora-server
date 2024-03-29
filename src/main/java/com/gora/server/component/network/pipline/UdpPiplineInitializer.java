package com.gora.server.component.network.pipline;

import com.gora.server.component.network.handler.inbound.ServerUdpMessageDecoder;
import com.gora.server.component.network.handler.inbound.UdpInboundHandler;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UdpPiplineInitializer extends ChannelInitializer<NioDatagramChannel> {
    private final UdpInboundHandler handler;
    

    @Override
    protected void initChannel(NioDatagramChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        EventExecutorGroup executor = new DefaultEventExecutorGroup(availableProcessors);
        ;
        pipeline
                .addLast("ServerUdpMessageDecoder", new ServerUdpMessageDecoder())
                .addLast(executor, "UdpInboundHandler", handler);
    }
}
