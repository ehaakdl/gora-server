package org.gora.server.component.network.pipline;

import org.gora.server.component.network.UdpInboundHandler;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UdpPiplineInitializer extends ChannelInitializer<NioDatagramChannel> {
    private final UdpInboundHandler handler;
    private final ServerUdpMessageDecoder decoder;
    @Override
    protected void initChannel(NioDatagramChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addFirst(decoder);
        pipeline.addLast(handler);
    }
}

