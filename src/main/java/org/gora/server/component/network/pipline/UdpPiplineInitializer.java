package org.gora.server.component.network.pipline;

import org.gora.server.component.network.ClientManager;
import org.gora.server.component.network.handler.inbound.ServerUdpMessageDecoder;
import org.gora.server.component.network.handler.inbound.UdpInboundHandler;
import org.gora.server.service.CloseClientResource;
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
    private final ClientManager clientManager;
    private final CloseClientResource closeClientResource;

    @Override
    protected void initChannel(NioDatagramChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        EventExecutorGroup executor = new DefaultEventExecutorGroup(availableProcessors);
        ;
        pipeline
                .addLast("ServerUdpMessageDecoder", new ServerUdpMessageDecoder(clientManager, closeClientResource))
                .addLast(executor, "UdpInboundHandler", handler);
    }
}
