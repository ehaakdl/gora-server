package org.gora.server.component.network.handler.inbound;

import java.util.ArrayList;
import java.util.List;

import org.gora.server.common.utils.AesUtils;
import org.gora.server.common.utils.CommonUtils;
import org.gora.server.common.utils.NetworkUtils;
import org.gora.server.component.network.ClientManager;
import org.gora.server.model.PacketRouterDTO;
import org.gora.server.model.network.NetworkPacketProtoBuf.NetworkPacket;
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
        
        if (isNotValidPacketSize(readableBytes)) {
            return;
        }


        byte[] recvBytes = new byte[in.readableBytes()];
        in.readBytes(recvBytes);

        List<PacketRouterDTO> PacketRouterDTOs;
        NetworkPacket packet = NetworkPacket.parseFrom(recvBytes);
        String channelId = packet.getChannelId();
        if (packet.getType() == eServiceType.udp_initial.getType()) {
            out.addAll(initClient(packet, datagramPacket));
            return;
        }

        // 패킷 조립
        try {
            PacketRouterDTOs = ClientManager.assemblePacket(AesUtils.decrypt(channelId), eNetworkType.udp, recvBytes);
        } catch (Exception e) {
            // 무조건 고정된 사이즈로 들어오기 때문에 캐스팅 실패할수가없다.
            log.error("위조된 패킷이 온걸로 추정됩니다. {}", CommonUtils.getStackTraceElements(e));
            log.info("패킷 위조 예상아이디 :{}", channelId);
            PacketRouterDTOs = new ArrayList<>(1);
            PacketRouterDTOs.add(PacketRouterDTO.create(eRouteServiceType.close_client, null, channelId));
        }

        if (PacketRouterDTOs.isEmpty()) {
            return;
        } else {
            out.addAll(PacketRouterDTOs);
        }
    }

    private boolean isNotValidPacketSize(int size) {
        return size != NetworkUtils.TOTAL_MAX_SIZE;
    }

    private List<PacketRouterDTO> initClient(NetworkPacket packet, DatagramPacket datagramPacket) {
        List<PacketRouterDTO> result;

        String channelId = NetworkUtils.generateChannelId();
        String clientIp = datagramPacket.sender().getAddress().getHostAddress();
        UdpInitialDTO udpInitialDTO = new UdpInitialDTO(clientIp);
        result = new ArrayList<>(1);
        result.add(0,
                PacketRouterDTO.create(eRouteServiceType.udp_initial, udpInitialDTO, channelId));

        return result;
    }
}
