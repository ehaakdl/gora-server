package org.gora.server.component.network.handler.inbound;

import java.util.ArrayList;
import java.util.List;

import org.gora.server.common.AesUtils;
import org.gora.server.common.CommonUtils;
import org.gora.server.common.NetworkUtils;
import org.gora.server.component.network.ClientManager;
import org.gora.server.model.TransportData;
import org.gora.server.model.network.NetworkPackcetProtoBuf.NetworkPacket;
import org.gora.server.model.network.UdpInitialDTO;
import org.gora.server.model.network.eNetworkType;
import org.gora.server.model.network.eRouteServiceType;
import org.gora.server.model.network.eServiceType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerUdpMessageDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> out) throws Exception {
        ByteBuf in = datagramPacket.content();
        int readableBytes = in.readableBytes();
        if (readableBytes != NetworkUtils.TOTAL_MAX_SIZE) {
            return;
        }
        byte[] recvBytes = new byte[in.readableBytes()];
        in.readBytes(recvBytes);

        List<TransportData> transportDatas;
        NetworkPacket packet = NetworkPacket.parseFrom(recvBytes);
        String channelId = packet.getChannelId();
        if (packet.getType() == eServiceType.udp_initial.getType()) {
            out.addAll(initClient(packet, datagramPacket));
            return;
        }

        // 패킷 조립
        try {
            transportDatas = ClientManager.assemblePacket(AesUtils.decrypt(channelId), eNetworkType.udp, recvBytes);
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

    private List<TransportData> initClient(NetworkPacket packet, DatagramPacket datagramPacket) {
        List<TransportData> result;

        String channelId = NetworkUtils.generateChannelId();
        String clientIp = datagramPacket.sender().getAddress().getHostAddress();
        UdpInitialDTO udpInitialDTO = new UdpInitialDTO(clientIp);
        result = new ArrayList<>(1);
        result.add(0,
                TransportData.create(eRouteServiceType.udp_initial, udpInitialDTO, channelId));

        return result;
    }
}
