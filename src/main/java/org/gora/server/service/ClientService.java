package org.gora.server.service;

import org.gora.server.common.utils.AesUtils;
import org.gora.server.common.utils.NetworkUtils;
import org.gora.server.component.network.ClientManager;
import org.gora.server.model.PacketRouterDTO;
import org.gora.server.model.network.ClientConnection;
import org.gora.server.model.network.NetworkPacketProtoBuf.NetworkPacket;
import org.gora.server.model.network.UdpInitialDTO;
import org.gora.server.model.network.eNetworkType;
import org.gora.server.model.network.eServiceType;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class ClientService {
    private final ClientManager clientManager;

    public void initialUdp(PacketRouterDTO PacketRouterDTO) {

        log.info(PacketRouterDTO.getChanelId());

        if (!(PacketRouterDTO.getData() instanceof UdpInitialDTO)) {
            log.error("udp init 실패 - 잘못된 데이터가 들어왔습니다.");
            return;
        }

        UdpInitialDTO udpInitialDTO = (UdpInitialDTO) PacketRouterDTO.getData();
        clientManager.putResource(PacketRouterDTO.getChanelId(),
                ClientConnection.createUdp(udpInitialDTO.getClientIp()));

        String encryptedChannelId = AesUtils.encrypt(PacketRouterDTO.getChanelId());

        NetworkPacket packet = NetworkUtils.getEmptyData(eServiceType.udp_initial,
                encryptedChannelId);

        boolean isSend = clientManager.send(eNetworkType.udp, eServiceType.udp_initial, packet,
                PacketRouterDTO.getChanelId());
        if (!isSend) {
            log.error("udp 클라이언트 식별값 전달 실패");
        }
    }

}
