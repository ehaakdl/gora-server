package org.gora.server.component.network.pipline;

import org.gora.server.component.network.TcpServerHandler;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TcpPiplineInitializer extends ChannelInitializer<SocketChannel>{
    private final TcpServerHandler handler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ServerTcpMessageDecoder());
        ch.pipeline().addLast(handler);
    }
}
