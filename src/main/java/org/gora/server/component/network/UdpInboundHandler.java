package org.gora.server.component.network;

import org.gora.server.model.CommonData;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
@Slf4j
public class UdpInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private final ObjectMapper objectMapper;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf buf = msg.content();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        CommonData content = objectMapper.readValue(bytes, CommonData.class);
        if(content == null){
            log.error("수신된 패킷 역직렬화 실패");
            return;
        }
        
        // 클라이언트 저장소에 무조건 아이피 저장
        content.setKey(msg.sender().getHostName());
        ClientManager.put(msg.sender().getHostName());
        PacketRouter.push(content);
    }
}