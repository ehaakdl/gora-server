package com.gora.server.service;

import com.gora.server.common.utils.AesUtils;
import com.gora.server.common.utils.NetworkUtils;
import com.gora.server.component.network.ClientManager;
import com.gora.server.model.PacketRouterDTO;
import com.gora.server.model.network.ClientConnection;
import com.gora.server.model.network.UdpInitialDTO;
import com.gora.server.model.network.eNetworkType;
import com.gora.server.model.network.eServiceType;
import com.gora.server.model.network.protobuf.NetworkPacketProtoBuf.NetworkPacket;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class ClientConnectionService {
    private final ClientManager clientManager;

    public void initialUdp(PacketRouterDTO PacketRouterDTO) {
        if (!(PacketRouterDTO.getData() instanceof UdpInitialDTO)) {
            log.error("udp init 실패 - 잘못된 데이터가 들어왔습니다.");
            return;
        }

        final UdpInitialDTO udpInitialDTO = (UdpInitialDTO) PacketRouterDTO.getData();
        final String channelId = PacketRouterDTO.getChannelId();
        final String clientIp = udpInitialDTO.getClientIp();
        final ClientConnection clientConnection = ClientConnection.createUdp(clientIp);
        clientManager.putResource(channelId, clientConnection);

        final String encryptedChannelId = AesUtils.encrypt(channelId);
        NetworkPacket packet = NetworkUtils.getEmptyData(eServiceType.udp_initial,
                encryptedChannelId);

        boolean isSend = clientManager.send(eNetworkType.udp, eServiceType.udp_initial, packet, channelId);
        if (!isSend) {
            log.error("udp 클라이언트 식별값 전달 실패");
        }
    }

}
