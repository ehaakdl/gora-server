package com.gora.server.component.network.handler.inbound;

import com.gora.server.common.utils.CommonUtils;
import com.gora.server.component.network.PacketRouter;
import com.gora.server.model.PacketRouterDTO;
import com.gora.server.model.exception.OverSizedException;
import com.gora.server.service.ClientCloseService;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class TcpInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final PacketRouterDTO packet = (PacketRouterDTO) msg;
        PacketRouter.push(packet);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("tcp handler error detail: {}", CommonUtils.getStackTraceElements(cause));
        if (cause instanceof OverSizedException) {
            log.warn("패킷 라우터 큐가 꽉 찼습니다. {}", PacketRouter.size());
        } else {
            ClientCloseService.close(ctx.channel().id().asLongText());
        }
    }

    // 종료 이벤트
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }
}
