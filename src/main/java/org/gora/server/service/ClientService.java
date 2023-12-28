package org.gora.server.service;

import org.gora.server.common.AesUtils;
import org.gora.server.common.NetworkUtils;
import org.gora.server.component.network.ClientManager;
import org.gora.server.model.TransportData;
import org.gora.server.model.network.ClientConnection;
import org.gora.server.model.network.NetworkPackcetProtoBuf.NetworkPacket;
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

    public void initialUdp(TransportData transportData) {

        log.info(transportData.getChanelId());

        if (!(transportData.getData() instanceof UdpInitialDTO)) {
            log.error("udp init 실패 - 잘못된 데이터가 들어왔습니다.");
            return;
        }

        UdpInitialDTO udpInitialDTO = (UdpInitialDTO) transportData.getData();
        clientManager.putResource(transportData.getChanelId(),
                ClientConnection.createUdp(udpInitialDTO.getClientIp()));

        String encryptedChannelId = AesUtils.encrypt(transportData.getChanelId());

        NetworkPacket packet = NetworkUtils.getEmptyData(eServiceType.udp_initial,
                encryptedChannelId);

        boolean isSend = clientManager.send(eNetworkType.udp, eServiceType.udp_initial, packet,
                transportData.getChanelId());
        if (!isSend) {
            log.error("udp 클라이언트 식별값 전달 실패");
        }
    }

}
