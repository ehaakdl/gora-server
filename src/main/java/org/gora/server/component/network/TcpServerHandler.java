package org.gora.server.component.network;

import java.util.List;

import org.gora.server.component.LoginTokenProvider;
import org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket;
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
        final List<NetworkPacket> packets = (List<NetworkPacket>) msg;
        for (NetworkPacket packet : packets) {
            try {
                PacketRouter.push(packet);
            } catch (IllegalStateException e) {
                log.warn("패킷 라우터 큐가 꽉 찼습니다. {}", PacketRouter.size());
            }    
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("tcp handler error detail: {}",cause.getCause());
        // ctx.close();
    }

    // 종료 이벤트
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }
}
