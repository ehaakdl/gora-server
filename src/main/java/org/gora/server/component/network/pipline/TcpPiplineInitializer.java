package org.gora.server.component.network.pipline;

import org.gora.server.component.network.handler.inbound.ServerTcpMessageDecoder;
import org.gora.server.component.network.handler.inbound.TcpActiveServerChannelUpdater;
import org.gora.server.component.network.handler.inbound.TcpInboundHandler;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TcpPiplineInitializer extends ChannelInitializer<SocketChannel> {
    private final TcpInboundHandler handler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new TcpActiveServerChannelUpdater());
        ch.pipeline().addLast(new ServerTcpMessageDecoder());
        ch.pipeline().addLast(handler);
    }
}
