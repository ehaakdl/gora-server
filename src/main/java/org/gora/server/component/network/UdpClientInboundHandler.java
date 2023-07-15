package org.gora.server.component.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.RequiredArgsConstructor;
import org.gora.server.model.CommonData;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class UdpClientInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private final PacketRouter packetRouter;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf buf = msg.content();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        CommonData content = CommonData.deserialization(bytes);

        PacketRouter.push(content);
    }
}