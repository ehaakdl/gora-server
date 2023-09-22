package org.gora.server.component.network;

import java.util.UUID;

import org.gora.server.common.NetworkUtils;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.CommonData;
import org.gora.server.model.eProtocol;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

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
// 파이프라인에 decoder 구성을 추가하였으나, decoder클래스를 거치지 않는 문제 발생
// decoder 역할은 임시로 handler에서 담당
public class UdpInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private final ObjectMapper objectMapper;
    private static StringBuilder assemble = new StringBuilder();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        CommonData commonData;
        try {
            byte[] contentByte = new byte[msg.content().readableBytes()];
            msg.content().readBytes(contentByte);

            String contentJson = new String(contentByte);
            assemble.append(contentJson);
            int index = assemble.indexOf(NetworkUtils.EOF);
            if (index < 0) {
                return;
            }

            String targetSerialize = assemble.substring(0, index);
            assemble.delete(0, index + NetworkUtils.EOF.length());

            commonData = objectMapper.readValue(targetSerialize, CommonData.class);
        } catch (Exception e) {
            log.error("[UDP] 잘못된 수신 패킷 왔습니다.", e);
            return;
        }
     
        commonData.setProtocol(eProtocol.udp);

        // 데이터에 key가 없으면 첫 송신이라고 생각, 디비나 캐시에 아이피 관리하는 방식으로 해야할듯
        if (!ClientManager.contain(commonData.getKey())) {
            commonData.setKey(msg.sender().getHostName());
            ClientConnection clientConnection = ClientConnection.createUdp(msg.sender().getHostName());
            String key = UUID.randomUUID().toString().replace("-", "");
            ClientManager.put(key, clientConnection);
            commonData.setKey(key);
        }

        try {
            PacketRouter.push(commonData);
        } catch (IllegalStateException e) {
            log.error("라우터 큐가 꽉 찼습니다. {}", e);
        }
    }
}