package org.gora.server.component.network.pipline;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.gora.server.common.NetworkUtils;
import org.gora.server.component.network.ClientManager;
import org.gora.server.model.ClientConnection;
import org.gora.server.model.TransportData;
import org.gora.server.model.network.NetworkPakcetProtoBuf.NetworkPacket;
import org.gora.server.model.network.eNetworkType;
import org.gora.server.model.network.eServiceType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ServerTcpMessageDecoder extends ByteToMessageDecoder {
    private final ClientManager clientManager;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf recvMsg, List<Object> outMsg) throws Exception {
        ByteBuf recvByteBuf = (ByteBuf) recvMsg;
        byte[] recvBytes = new byte[recvByteBuf.readableBytes()];
        recvMsg.readBytes(recvBytes);

        String chanelId = ctx.channel().id().asLongText();
        List<NetworkPacket> packets;
        if(!clientManager.existsResource(chanelId)){
            String clientIp = ((InetSocketAddress)ctx.channel().remoteAddress()).getHostName();
            ClientConnection connection = ClientConnection.createTcp(clientIp, ctx);
            clientManager.createResource(chanelId, connection);
        }
        
        // 패킷 조립
        try {
            packets = clientManager.assemblePacket(chanelId, eNetworkType.tcp, recvBytes);
        } catch (Exception e) {
            // 무조건 고정된 사이즈로 들어오기 때문에 캐스팅 실패할수가없다.
            log.error("위조된 패킷이 온걸로 추정됩니다.");
            TransportData transportData = TransportData.builder()
            .chanelId(chanelId)
            .packet(NetworkUtils.getEmptyData(eServiceType.close_client, chanelId))
            .build();
            outMsg.add(transportData);
            return;
        }

        if (packets == null) {
            return;
        } else {
            // 서버내 객체로 컨버터
            List<TransportData> transportDatas = new ArrayList<>();
            for (NetworkPacket packet : packets) {
                transportDatas.add(
                    TransportData.builder()
                    .chanelId(chanelId)
                    .packet(packet)
                    .build()
                );
            }

            outMsg.add(transportDatas);
        }
    }

}
