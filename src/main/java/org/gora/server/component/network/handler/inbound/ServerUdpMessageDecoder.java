package org.gora.server.component.network.handler.inbound;

import java.util.ArrayList;
import java.util.List;

import org.gora.server.common.CommonUtils;
import org.gora.server.component.network.ClientManager;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.TransportData;
import org.gora.server.model.eRouteServiceType;
import org.gora.server.model.network.eNetworkType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ServerUdpMessageDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) throws Exception {
        ByteBuf in = packet.content();
        int readableBytes = in.readableBytes();
        if (readableBytes <= 0) {
            return;
        }
        byte[] recvBytes = new byte[in.readableBytes()];
        in.readBytes(recvBytes);
        String channelId = ctx.channel().id().asLongText();
        List<TransportData> transportDatas;
        if (!ClientManager.existsResource(channelId)) {
            String clientIp = packet.sender().getAddress().getHostAddress();
            ClientConnection connection = ClientConnection.createUdp(clientIp);
            ClientManager.createResource(channelId, connection);
        }
        // 패킷 조립
        try {
            transportDatas = ClientManager.assemblePacket(channelId, eNetworkType.udp, recvBytes);
        } catch (Exception e) {
            // 무조건 고정된 사이즈로 들어오기 때문에 캐스팅 실패할수가없다.
            log.error("위조된 패킷이 온걸로 추정됩니다. {}", CommonUtils.getStackTraceElements(e));
            log.info("패킷 위조 예상아이디 :{}", channelId);
            transportDatas = new ArrayList<>(1);
            transportDatas.add(TransportData.create(eRouteServiceType.close_client, null, channelId));
        }

        if (transportDatas.isEmpty()) {
            return;
        } else {
            out.addAll(transportDatas);
        }
    }
}
