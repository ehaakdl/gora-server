package org.gora.server.component.network;

import java.util.UUID;

import org.gora.server.model.ClientConnection;
import org.gora.server.model.CommonData;
import org.gora.server.model.eProtocol;
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

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CommonData commonData = (CommonData) msg;
        commonData.setProtocol(eProtocol.tcp);

        // 첫 연결인 경우 클라이언트 맵에 추가
        String key = UUID.randomUUID().toString().replace("-", "");
        if (!ClientManager.contain(commonData.getKey())) {
            ClientManager.put(key, ClientConnection.createTcp(key, ctx));
            commonData.setKey(key);
        }

        try {
            PacketRouter.push(commonData);
        } catch (IllegalStateException e) {
            log.error("패킷 라우터 큐가 꽉 찼습니다. {}", PacketRouter.size());
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
