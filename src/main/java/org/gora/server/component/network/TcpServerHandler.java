package org.gora.server.component.network;

import org.gora.server.component.LoginTokenProvider;
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
    private final LoginTokenProvider loginTokenProvider;
    private final ClientManager clientManager;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NetworkPacket networkPacket = (NetworkPacket) msg;
        networkPacket.setProtocol(eProtocol.tcp);

        String key = networkPacket.getKey();
        if(!loginTokenProvider.validToken(key)){
            log.warn("잘못된 토큰입니다. {}", key);
            ctx.close();
            return;
        }

        // 첫 연결인 경우 클라이언트 맵에 추가
        if (!clientManager.contain(key)) {
            String clientIp = ctx.channel().remoteAddress().toString();
            clientManager.put(clientIp, ClientConnection.createTcp(key, ctx));
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
        log.error("tcp handler error detail: {}",cause.getCause());
        ctx.close();
    }

    // 종료 이벤트
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }
}
