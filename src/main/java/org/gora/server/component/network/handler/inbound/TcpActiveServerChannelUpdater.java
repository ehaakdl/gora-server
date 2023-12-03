package org.gora.server.component.network.handler.inbound;

import org.gora.server.component.network.ClientManager;
import org.gora.server.model.ClientConnection;
import org.gora.server.service.CloseClientResource;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TcpActiveServerChannelUpdater extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String channelId = ctx.channel().id().asLongText();
        if (!ClientManager.existsResource(channelId)) {
            ClientConnection connection = ClientConnection.createTcp(ctx);
            ClientManager.createResource(channelId, connection);
        }

        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        CloseClientResource.close(ctx.channel().id().asLongText());
        ctx.fireChannelInactive();
    }
}