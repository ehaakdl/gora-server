package org.gora.server.component.network;

import java.util.ArrayList;
import java.util.List;

import org.gora.server.common.CommonUtils;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.TransportData;
import org.gora.server.model.network.eNetworkType;
import org.gora.server.model.network.eServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private ClientManager clientManager;

    // 순환참조로 clientManager 부분은 객체 생성이후에 주입받는다.
    @Autowired
    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        byte[] recvBytes = new byte[msg.content().readableBytes()];
        msg.content().readBytes(recvBytes);

        String chanelId = ctx.channel().id().asLongText();
        List<TransportData> transportDatas = new ArrayList<>();
        if (!clientManager.existsResource(chanelId)) {
            String clientIp = msg.recipient().getHostName();
            ClientConnection connection = ClientConnection.createUdp(clientIp);
            clientManager.createResource(chanelId, connection);
        }

        // 패킷 조립
        try {
            transportDatas = clientManager.assemblePacket(chanelId, eNetworkType.udp, recvBytes);
        } catch (Exception e) {
            // 무조건 고정된 사이즈로 들어오기 때문에 캐스팅 실패할수가없다.
            log.error("위조된 패킷이 온걸로 추정됩니다. {}", CommonUtils.getStackTraceElements(e));
            TransportData transportData = TransportData.builder()
                    .chanelId(chanelId)
                    .data(TransportData.create(eServiceType.close_client, null, chanelId))
                    .build();
            transportDatas.add(transportData);
        }

        transportDatas.forEach(transportData -> {
            try {
                PacketRouter.push(transportData);
            } catch (IllegalStateException e) {
                log.warn("패킷 라우터 큐가 꽉 찼습니다. {}", PacketRouter.size());
            }
        });
    }
}