package org.gora.server.component.network.handler.inbound;

import org.gora.server.common.CommonUtils;
import org.gora.server.component.network.PacketRouter;
import org.gora.server.model.TransportData;
import org.gora.server.model.exception.OverSizedException;
import org.gora.server.service.CloseClientResource;
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
public class TcpInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final TransportData packet = (TransportData) msg;
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
            CloseClientResource.close(ctx.channel().id().asLongText());
        }
    }

    // 종료 이벤트
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }
}
