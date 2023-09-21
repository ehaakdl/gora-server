package org.gora.server.component.network;

import java.util.UUID;

import org.gora.server.model.ClientConnection;
import org.gora.server.model.CommonData;
import org.gora.server.model.eProtocol;
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
        content.setProtocol(eProtocol.udp);
        
        // 데이터에 key가 없으면 첫 송신이라고 생각, 디비나 캐시에 아이피 관리하는 방식으로 해야할듯
        if (!ClientManager.contain(content.getKey())) {
            content.setKey(msg.sender().getHostName());
            ClientConnection clientConnection = ClientConnection.createUdp(msg.sender().getHostName());
            String key = UUID.randomUUID().toString().replace("-", "");
            ClientManager.put(key ,clientConnection);
            content.setKey(key);
        }
        
        try{
            PacketRouter.push(content);
        }catch(IllegalStateException e){
            log.error("송싱 큐가 꽉 찼습니다. {}", e);
        }
    }
}