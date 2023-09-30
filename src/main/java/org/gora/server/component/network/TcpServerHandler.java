package org.gora.server.component.network;

import org.gora.server.component.TokenProvider;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.eProtocol;
import org.gora.server.model.network.NetworkPacket;
import org.springframework.stereotype.Component;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
@Slf4j
public class TcpServerHandler extends ChannelInboundHandlerAdapter {
    private final TokenProvider tokenProvider;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NetworkPacket networkPacket = (NetworkPacket) msg;
        networkPacket.setProtocol(eProtocol.tcp);

        String key = networkPacket.getKey();
        if(!tokenProvider.validToken(key)){
            log.warn("not valid token {}", key);
            ctx.close();
            return;
        }

        // 첫 연결인 경우 클라이언트 맵에 추가
        if (!ClientManager.contain(key)) {
            ClientManager.put(key, ClientConnection.createTcp(key, ctx));
            networkPacket.setKey(key);
        }

        try {
            PacketRouter.push(networkPacket);
        } catch (IllegalStateException e) {
            log.warn("패킷 라우터 큐가 꽉 찼습니다. {}", PacketRouter.size());
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    // 종료 이벤트
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }
}
