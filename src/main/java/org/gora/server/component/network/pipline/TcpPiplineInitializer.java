package org.gora.server.component.network.pipline;

import org.gora.server.component.network.ClientManager;
import org.gora.server.component.network.TcpInboundHandler;
import org.gora.server.service.CloseClientResource;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TcpPiplineInitializer extends ChannelInitializer<SocketChannel> {
    private final TcpInboundHandler handler;
    private final ClientManager clientManager;
    private final CloseClientResource closeClientResource;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ServerTcpMessageDecoder(clientManager, closeClientResource));
        ch.pipeline().addLast(handler);
    }
}
