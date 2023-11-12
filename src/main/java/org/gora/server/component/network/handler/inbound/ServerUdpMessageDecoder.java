package org.gora.server.component.network.handler.inbound;

import java.util.List;

import org.gora.server.common.CommonUtils;
import org.gora.server.component.network.ClientManager;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.TransportData;
import org.gora.server.model.network.eNetworkType;
import org.gora.server.service.CloseClientResource;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ServerUdpMessageDecoder extends MessageToMessageDecoder<DatagramPacket> {
    private final ClientManager clientManager;
    private final CloseClientResource closeClientResource;

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
        if (!clientManager.existsResource(channelId)) {
            String clientIp = packet.recipient().getHostName();
            ClientConnection connection = ClientConnection.createUdp(clientIp);
            clientManager.createResource(channelId, connection);
        }
        // 패킷 조립
        try {
            transportDatas = clientManager.assemblePacket(channelId, eNetworkType.udp, recvBytes);
        } catch (Exception e) {
            // 무조건 고정된 사이즈로 들어오기 때문에 캐스팅 실패할수가없다.
            log.error("위조된 패킷이 온걸로 추정됩니다. {}", CommonUtils.getStackTraceElements(e));
            log.info("패킷 위조 예상아이디 :{}", channelId);
            closeClientResource.close(channelId);
            return;
        }

        if (transportDatas.isEmpty()) {
            return;
        } else {
            out.addAll(transportDatas);
        }
    }
}
