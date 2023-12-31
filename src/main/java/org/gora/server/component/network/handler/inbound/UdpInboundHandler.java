package org.gora.server.component.network.handler.inbound;

import org.gora.server.common.CommonUtils;
import org.gora.server.component.network.PacketRouter;
import org.gora.server.model.TransportData;
import org.gora.server.model.exception.OverSizedException;
import org.gora.server.service.CloseClientResource;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
@Slf4j
public class UdpInboundHandler extends SimpleChannelInboundHandler<TransportData> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransportData msg) throws Exception {
        try {
            PacketRouter.push(msg);
        } catch (OverSizedException e) {
            log.warn("패킷 라우터 큐가 꽉 찼습니다. {}", PacketRouter.size());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("udp handler error detail: {}", CommonUtils.getStackTraceElements(cause));
        if (cause instanceof OverSizedException) {
            log.warn("패킷 라우터 큐가 꽉 찼습니다. {}", PacketRouter.size());
        } else {
            CloseClientResource.close(ctx.channel().id().asLongText());
        }
    }
}