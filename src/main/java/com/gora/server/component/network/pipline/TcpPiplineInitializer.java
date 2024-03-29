package com.gora.server.component.network.pipline;

import com.gora.server.component.network.handler.inbound.ServerTcpMessageDecoder;
import com.gora.server.component.network.handler.inbound.TcpActiveServerChannelUpdater;
import com.gora.server.component.network.handler.inbound.TcpInboundHandler;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TcpPiplineInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new TcpActiveServerChannelUpdater());
        ch.pipeline().addLast(new ServerTcpMessageDecoder());
        ch.pipeline().addLast(new TcpInboundHandler());
    }
}
