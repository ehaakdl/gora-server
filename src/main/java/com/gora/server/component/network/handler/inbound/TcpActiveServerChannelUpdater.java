package com.gora.server.component.network.handler.inbound;

import com.gora.server.component.network.ClientManager;
import com.gora.server.model.network.ClientConnection;
import com.gora.server.service.ClientCloseService;

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
            ClientManager.putResource(channelId, connection);
        }

        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ClientCloseService.close(ctx.channel().id().asLongText());
        ctx.fireChannelInactive();
    }
}