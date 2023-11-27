package org.gora.server.component.network.handler.inbound;

import java.net.InetSocketAddress;

import org.gora.server.component.network.ClientManager;
import org.gora.server.model.ClientConnection;
import org.gora.server.service.CloseClientResource;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TcpActiveServerChannelUpdater extends ChannelInboundHandlerAdapter {
    private final ClientManager clientManager;
    private final CloseClientResource closeClientResource;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String channelId = ctx.channel().id().asLongText();
        if (!clientManager.existsResource(channelId)) {
            String clientIp = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostName();
            ClientConnection connection = ClientConnection.createTcp(clientIp, ctx);
            clientManager.createResource(channelId, connection);
        }

        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        closeClientResource.close(ctx.channel().id().asLongText());
        ctx.fireChannelInactive();
    }
}